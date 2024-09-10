package org.project.sellbook.controller;

import org.project.sellbook.model.Author;
import org.project.sellbook.model.Book;
import org.project.sellbook.DTO.BookDto;
import org.project.sellbook.model.Category;
import org.project.sellbook.repository.AuthorRepository;
import org.project.sellbook.repository.BookRepository;
import org.project.sellbook.repository.CategoryRepository;
import org.project.sellbook.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Controller
public class BookController {

    private final BookService bookService;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;

    public BookController(BookService bookService, BookRepository bookRepository,
            CategoryRepository categoryRepository, AuthorRepository authorRepository) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
    }

    @GetMapping("/books")
    public String getAllBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "product";
    }

    // View book detail
    @GetMapping("/detail/{id}")
    public String bookDetail(@PathVariable("id") Integer id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        model.addAttribute("books", getRelatedBooks(book));

        return "book_detail";
    }

    // get related books
    private List<Book> getRelatedBooks(Book book) {
        Integer author_id = book.getAuthor().getId();
        Optional<Author> author = authorRepository.findById(author_id);
        return author.map(bookRepository::findByAuthor).orElse(null);
    }

    @GetMapping("/books/author/{authorId}")
    private String filterBookByAuthor(@PathVariable("authorId") Integer authorId, Model model) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid author Id" + authorId));
        List<Book> books = bookRepository.findByAuthor(author);
        model.addAttribute("author", author);
        model.addAttribute("books", books);
        return "books_author";
    }

    @GetMapping("/create-book")
    public String showCreateBookForm(Model model) {
        model.addAttribute("book", new BookDto());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("authors", authorRepository.findAll());
        return "create_book";
    }

    @PostMapping("/create-book")
    public String createBookPost(@ModelAttribute BookDto bookDto) throws IOException {
        MultipartFile image = bookDto.getImage();
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));

        try {
            String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + fileName), StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException ioe) {
            throw new IOException("Could not save image file " + fileName);
        }
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setCategory(bookDto.getCategory());
        book.setPrice(bookDto.getPrice());
        book.setDescription(bookDto.getDescription());
        book.setImage(fileName);
        book.setStock(bookDto.getStock());

        bookRepository.save(book);
        return "redirect:/home";
    }

    @GetMapping("/shop")
    public String shop(Model model) {
        List<Book> books = bookService.getAllBooks();
        addCommonAttributes(model, books);
        return "shop/shop";
    }

    @GetMapping("/shop/filter")
    public String filterBookByCategory(@RequestParam List<String> categoryNames, Model model) {
        List<Category> categoriesFilter = categoryRepository.findByNameIn(categoryNames);
        List<Book> books = bookRepository.findByCategoryIn(categoriesFilter);

        addCommonAttributes(model, books);
        return "shop/filterBook";
    }

    @GetMapping("/shop/filter/books/authors")
    public String filterBookByAuthor(@RequestParam List<String> authorNames, Model model) {
        List<Author> authorsFilter = authorRepository.findByNameIn(authorNames);
        List<Book> books = bookRepository.findByAuthorIn(authorsFilter);
        addCommonAttributes(model, books);
        return "shop/filterBook";
    }

    private void addCommonAttributes(Model model, List<Book> books) {
        List<Category> categories = categoryRepository.findAll();
        List<Author> authors = authorRepository.findAll();

        Map<Category, Long> bookCountByCategory = new HashMap<>();
        for (Category category : categories) {
            long count = bookRepository.countByCategory(category);
            bookCountByCategory.put(category, count);
        }

        Map<Author, Long> bookCountByAuthor = new HashMap<>();
        for (Author author : authors) {
            long count = bookRepository.countByAuthor(author);
            bookCountByAuthor.put(author, count);
        }

        model.addAttribute("books", books);
        model.addAttribute("bookCountByCategory", bookCountByCategory);
        model.addAttribute("bookCountByAuthor", bookCountByAuthor);
    }
}

package org.project.sellbook.controller;


import org.project.sellbook.model.BestSellingBook;
import org.project.sellbook.model.Book;
import org.project.sellbook.repository.BookRepository;
import org.project.sellbook.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    private final BookService bookService;
    private final BookRepository bookRepository;

    public MainController(BookService bookService, BookRepository bookRepository) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public String home() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String index(Model model) {
        List<Book> books = bookService.getAllBooks();

        List<Book> bestSellingBooks = bookRepository.getBestSellingBooks();

        model.addAttribute("books", books);
        model.addAttribute("bestSellingBooks", bestSellingBooks);
        return "index";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin_page";
    }

    @GetMapping("/order")
    public String order() {
        return "order";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

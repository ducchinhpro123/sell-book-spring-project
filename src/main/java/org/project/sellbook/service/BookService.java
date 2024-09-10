package org.project.sellbook.service;

import org.project.sellbook.model.Book;
import org.project.sellbook.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<Book> getAllBooks() {
        return repository.findAll();
    }

    public Book getBookById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    /*
     * public List<Book> getBestSellingBooks() {
     * List<BestSellingBook> bestSellingBooks = bestSellingBookRepository.findAll();
     * return
     * bestSellingBooks.stream().map(BestSellingBook::getBook).collect(Collectors.
     * toList());
     * }
     */
}

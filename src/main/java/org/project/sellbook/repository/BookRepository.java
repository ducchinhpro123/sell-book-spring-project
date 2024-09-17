package org.project.sellbook.repository;

import org.project.sellbook.model.Author;
import org.project.sellbook.model.Book;
import org.project.sellbook.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {
    @Query("SELECT b FROM Book b WHERE b.author = :author")
    List<Book> findByAuthor(Author author);

    @Query("SELECT b FROM BestSellingBook bs JOIN Book b ON bs.book = b")
    List<Book> getBestSellingBooks();

    long countByCategory(Category category);

    long countByAuthor(Author author);

    List<Book> findBookByCategory(Category category);

    List<Book> findByCategoryIn(List<Category> categories);

    List<Book> findByAuthorIn(List<Author> authors);

    @Query("SELECT b FROM Book b where b.title like %:title%")
    List<Book> findBookByTitle(String title);
}

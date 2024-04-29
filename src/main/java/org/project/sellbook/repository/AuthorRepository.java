package org.project.sellbook.repository;

import org.project.sellbook.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
    public Author getAuthorById(Integer id);

    List<Author> findByNameIn(List<String> authorNames);
}

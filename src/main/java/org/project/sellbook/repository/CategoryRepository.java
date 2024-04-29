package org.project.sellbook.repository;

import org.project.sellbook.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findCategoryByName(String name);

    List<Category> findByNameIn(List<String> name);
}

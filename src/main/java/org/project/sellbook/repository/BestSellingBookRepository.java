package org.project.sellbook.repository;

import org.project.sellbook.model.BestSellingBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BestSellingBookRepository extends JpaRepository<BestSellingBook, Integer> {
}

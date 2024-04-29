package org.project.sellbook.repository;


import org.project.sellbook.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository  extends JpaRepository<User, Integer>{
    @Query("SELECT u FROM User u WHERE u.username = :username")
    public User findUserByUsername(String username);
}

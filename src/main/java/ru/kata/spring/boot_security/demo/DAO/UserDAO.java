package ru.kata.spring.boot_security.demo.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;

@Repository
public interface UserDAO extends JpaRepository<User, Long> {
    User findByUsername(String username);
}




    /*

    List<User> getAllUsers();

    void save(User user);

    User userById(int id);

    void update(int id, User updatedUser);

    void delete(int id);

}
     */
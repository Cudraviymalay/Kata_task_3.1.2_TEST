package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;
import java.util.Set;

public interface UserService {

    List<User> getAllUsers();

    void save(User user);

    User userById(Long id);

    User createUser(User user, Set<Role> roles);

    void updateUser(Long id, User userFromRequest, Set<Role> roles);

    User getUserInfo();

    void delete(Long id);

    Role getRole(String role);
}
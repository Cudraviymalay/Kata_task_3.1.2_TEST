package ru.kata.spring.boot_security.demo.init_postconstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.DAO.RoleDAO;
import ru.kata.spring.boot_security.demo.DAO.UserDAO;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Component
public class InitPostConstruct {

    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public InitPostConstruct(UserDAO userDAO, RoleDAO roleDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initializationTable() {

        Role roleAdmin = roleDAO.findByName("ROLE_ADMIN");
        Role roleUser = roleDAO.findByName("ROLE_USER");

        if (roleAdmin == null) {
            roleAdmin = new Role("ROLE_ADMIN");
            roleDAO.save(roleAdmin);
        }
        if (roleUser == null) {
            roleUser = new Role("ROLE_USER");
            roleDAO.save(roleUser);
        }

        if (userDAO.findByUsername("admin") == null) {
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(roleAdmin);

            User admin = new User();
            admin.setUsername("admin");
            admin.setSurname("admin");
            admin.setAge(28);
            admin.setEmail("admin@mail.ru");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRoles(adminRoles);
            userDAO.save(admin);
        }

        if (userDAO.findByUsername("user") == null) {
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(roleUser);

            User user = new User();
            user.setUsername("user");
            user.setSurname("user");
            user.setAge(28);
            user.setEmail("user@mail.ru");
            user.setPassword(passwordEncoder.encode("user"));
            user.setRoles(userRoles);
            userDAO.save(user);
        }
    }
}
/*
package ru.kata.spring.boot_security.demo.init_postconstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.DAO.RoleDAO;
import ru.kata.spring.boot_security.demo.DAO.UserDAO;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import javax.transaction.Transactional;
import java.util.Optional;

@Component
public class InitPostConstruct implements CommandLineRunner {

    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserDAO userDAO, RoleDAO roleDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        Role adminRole = createRoleIfNotExists("ROLE_ADMIN");
        Role userRole = createRoleIfNotExists("ROLE_USER");

        createUserIfNotExists("admin", "admin", 28,"admin", "admin@mai.ru", adminRole);
        createUserIfNotExists("user", "user", 28, "user","user@mai.ru", userRole);
    }

    @Transactional
    public Role createRoleIfNotExists(String roleName) {
        Role existingRole = roleDAO.findByName(roleName);
        if (existingRole != null) {
            return existingRole;
        } else {
            Role newRole = new Role();
            newRole.setName(roleName);
            return roleDAO.save(newRole);
        }
    }

    @Transactional
    public void createUserIfNotExists(String username, String surname, int age, String password, String email, Role role) {
        Optional<User> existingUser = userDAO.findByUsername(username);
        if (existingUser.isPresent()) {
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setSurname(surname);
        user.setAge(age);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.getRoles().add(role);

        try {
            userDAO.save(user);
        } catch (Exception e) {
            System.out.println("Ошибка при сохранении пользователя: " + e.getMessage());
        }
    }
}

 */


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
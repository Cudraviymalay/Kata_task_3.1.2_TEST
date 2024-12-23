package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.DAO.UserDAO;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserDAO userDAO;

    @Autowired
    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    @Override
    @Transactional
    public void save(User user) {
        userDAO.save(user);
    }

    @Override
    @Transactional
    public User userById(Long id) {
        return userDAO.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public User createUser(User user, Set<Role> roles) {
        return null;
    }

    @Override
    @Transactional
    public void update(Long id, User userFromRequest) {

        User userToUpdate = userDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));

        userToUpdate.setUsername(userFromRequest.getUsername());
        userToUpdate.setSurname(userFromRequest.getSurname());
        userToUpdate.setAge(userFromRequest.getAge());
        userToUpdate.setPassword(userFromRequest.getPassword());
        userToUpdate.setEmail(userFromRequest.getEmail());

        userDAO.save(userToUpdate);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userDAO.deleteById(id);
    }
}

        /*

    @Transactional
    @PostConstruct
    public void init() {
        // Создаем роли, только если они не существуют
        Role roleUser = roleDAO.findByName("ROLE_USER");
        if (roleUser == null) {
            roleUser = new Role("ROLE_USER");
            roleDAO.save(roleUser);
        }

        Role roleAdmin = roleDAO.findByName("ROLE_ADMIN");
        if (roleAdmin == null) {
            roleAdmin = new Role("ROLE_ADMIN");
            roleDAO.save(roleAdmin);
        }

        if (userDAO.findByUsername("user") == null) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user"));
            user.setRoles(Set.of(roleUser));
            userDAO.save(user);
        }

        if (userDAO.findByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRoles(Set.of(roleUser, roleAdmin));
            userDAO.save(admin);
        }
    }

     */
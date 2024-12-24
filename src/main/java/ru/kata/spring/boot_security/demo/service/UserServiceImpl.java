package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.DAO.UserDAO;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserDAO userDAO;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    public UserServiceImpl(UserDAO userDAO, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userDAO.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        }
        return new org.springframework.security.core.userdetails.User(user.get().getUsername(),
                user.get().getPassword(),
                mapRolesToAuthorities(user.get().getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    @Transactional
    @Override
    public void save(User user) {
        userDAO.save(user);
    }

    @Transactional
    @Override
    public User userById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userService = (UserDetails) authentication.getPrincipal();
        return (User) authentication.getPrincipal();
    }

    @Transactional
    @Override
    public User createUser(User user, Set<Role> roles) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> setOfRoles = roles.stream()
                .map(role -> {
                    Role foundRole = roleService.findByName(role.getName());
                    if (foundRole == null) {
                        throw new EntityNotFoundException("Role " + role.getName() + " not found");
                    }
                    return foundRole;
                })
                .collect(Collectors.toSet());

        user.setRoles(setOfRoles);
        return user;
    }

    @Transactional
    @Override
    public void updateUser(Long id, User userFromRequest, Set<Role> roles) {
        User userToUpdate = userDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));

        userToUpdate.setUsername(userFromRequest.getUsername());
        userToUpdate.setSurname(userFromRequest.getSurname());
        userToUpdate.setAge(userFromRequest.getAge());
        userToUpdate.setEmail(userFromRequest.getEmail());

        if (userFromRequest.getPassword() != null && !userFromRequest.getPassword().isEmpty()) {
            if (!passwordEncoder.matches(userFromRequest.getPassword(), userToUpdate.getPassword())) {
                userToUpdate.setPassword(passwordEncoder.encode(userFromRequest.getPassword()));
            }
        }
        if (roles != null && !roles.isEmpty()) {
            Set<Role> updatedRoles = roles.stream()
                    .map(role -> {
                        Role foundRole = roleService.findByName(role.getName());
                        if (foundRole == null) {
                            throw new EntityNotFoundException("Role " + role.getName() + " not found");
                        }
                        return foundRole;
                    })
                    .collect(Collectors.toSet());
            userToUpdate.setRoles(updatedRoles);
        }
        userDAO.save(userToUpdate);
    }


    @Transactional
    @Override
    public Optional<User> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            return userDAO.findByUsername(userDetails.getUsername());
        } else {
            throw new IllegalStateException("Principal is not of type UserDetails");
        }
    }

    @Transactional
    @Override
    public void delete(Long id) {
        userDAO.deleteById(id);
    }

    @Transactional
    public Role getRole(String role) {
        return roleService.findByName(role);
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
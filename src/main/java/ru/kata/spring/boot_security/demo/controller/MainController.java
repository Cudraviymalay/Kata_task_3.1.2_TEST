package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class MainController {

    final UserService userService;
    final RoleService roleService;

    public MainController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/admin/")
    public String getUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }

    @GetMapping("/admin/new/")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        return "new";
    }

    @GetMapping("/admin/edit/{id}")
    public String edit(Model model, @PathVariable("id") Long id) {
        model.addAttribute("user", userService.userById(id));
        return "edit";
    }


    @PostMapping("/admin/new/")
    public String addUser(@ModelAttribute User user, Model model) {
        model.addAttribute("user", user);
        userService.save(user);
        return "redirect:/users";
    }

    @PostMapping("/admin/update/")
    public String update(@ModelAttribute("user") User user,
                         @RequestParam(value = "role", required = false) Set<String> roleNames) {
        Set<Role> roles = roleNames != null
                ? roleNames.stream()
                .map(roleName -> roleService.findByName(roleName))
                .filter(role -> role != null)
                .collect(Collectors.toSet())
                : null;

        userService.updateUser(user.getId(), user, roles);
        return "redirect:/admin/admin";
    }

    @PostMapping("/delete/")
    public String delete(@RequestParam("id") Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }

    @GetMapping("/user")
    public String userById(Model model) {
        Optional<User> user = userService.getUserInfo();
        model.addAttribute("user", user);
        return "userbyid";
    }
}
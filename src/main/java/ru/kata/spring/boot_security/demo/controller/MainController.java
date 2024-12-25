package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Set;

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

    @GetMapping(value = "/admin/edit/")
    public String edit(@RequestParam(value = "id") long id, Model model) {
        model.addAttribute("user", userService.getOne(id));
        return "edit";
    }

    @PostMapping("/admin/new/")
    public String addUser(@ModelAttribute User user, @RequestParam(value = "role") Set<Role> roles) {
        userService.save(userService.createUser(user, roles));
        return "redirect:/admin/";
    }

    @PostMapping("/admin/update/")
    public String update(@ModelAttribute("user") User user,
                         @RequestParam(value = "role", required = false) Set<Role> roleNames,
                         @RequestParam(value = "id") long id) {
        userService.update(id, userService.updateUser(id, user, roleNames));
        return "redirect:/admin/";
    }

    @PostMapping("/delete/")
    public String delete(@RequestParam("id") long id) {
        userService.delete(id);
        return "redirect:/admin/";
    }

    @GetMapping("/user")
    public String userById(Model model) {
        model.addAttribute("user", userService.getUserInfo());
        return "userbyid";
    }
}
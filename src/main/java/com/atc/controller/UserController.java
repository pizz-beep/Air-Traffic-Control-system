package com.atc.controller;

import com.atc.model.User;
import com.atc.service.FlightService;
import com.atc.service.RunwayService;
import com.atc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FlightService flightService;
    private final RunwayService runwayService;

    // GET /users  → admin view all users
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("pilotCount", userService.countByRole(User.Role.PILOT));
        model.addAttribute("atcCount", userService.countByRole(User.Role.ATC_CONTROLLER));
        model.addAttribute("adminCount", userService.countByRole(User.Role.ADMINISTRATOR));
        model.addAttribute("totalFlights", flightService.getAllFlights().size());
        return "user/list";
    }

    // GET /login  → login page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // GET /users/create → show create user form
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("roles", User.Role.values());
        return "user/create";
    }

    // POST /users/create → create a new user
    @PostMapping("/create")
    public String createUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam User.Role role,
            @RequestParam(required = false) String licenseNumber,
            @RequestParam(required = false) String stationCode,
            RedirectAttributes redirectAttributes) {
        try {
            userService.createUser(name, email, password, role, licenseNumber, stationCode);
            redirectAttributes.addFlashAttribute("successMsg",
                "User '" + name + "' created successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/users";
    }

    // POST /users/{id}/delete  → admin removes user
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMsg", "User deleted.");
        return "redirect:/users";
    }
}


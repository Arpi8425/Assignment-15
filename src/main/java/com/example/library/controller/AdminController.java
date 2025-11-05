package com.example.library.controller;

import com.example.library.model.Book;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class AdminController {

    @GetMapping("/admin/reports")
    public String adminReports() {
        return "Admin reports: total books = " + 2;
    }

    @GetMapping("/users")
    public List<Map<String, String>> listUsers() {
        return List.of(
                Map.of("username", "admin", "role", "ADMIN"),
                Map.of("username", "librarian", "role", "LIBRARIAN"),
                Map.of("username", "student", "role", "STUDENT"),
                Map.of("username", "guest", "role", "GUEST")
        );
    }
}

package com.example.library.controller;

import com.example.library.model.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/books")
public class BookController {

    private final Map<Long, Book> bookRepo = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(3); // Start at 3 (assuming 2 sample books added initially)

    public BookController() {
        bookRepo.put(1L, new Book(1L, "1984", "George Orwell", "ISBN001", true));
        bookRepo.put(2L, new Book(2L, "Brave New World", "Aldous Huxley", "ISBN002", true));
    }

    // ✅ Public endpoint - no role required
    @GetMapping("/public")
    public List<Book> getPublicBooks() {
        List<Book> publicList = new ArrayList<>();
        for (Book b : bookRepo.values()) {
            publicList.add(new Book(b.getId(), b.getTitle(), b.getAuthor(), null, b.isAvailable())); // hiding ISBN
        }
        return publicList;
    }

    // ✅ View all books - STUDENT and above
    @PreAuthorize("hasAnyRole('STUDENT','LIBRARIAN','ADMIN')")
    @GetMapping
    public Collection<Book> getAllBooks() {
        return bookRepo.values();
    }

    // ✅ View specific book - STUDENT and above
    @PreAuthorize("hasAnyRole('STUDENT','LIBRARIAN','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book book = bookRepo.get(id);
        if (book == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(book);
    }

    // ✅ Reserve book - STUDENT and above
    @PreAuthorize("hasAnyRole('STUDENT','LIBRARIAN','ADMIN')")
    @PostMapping("/{id}/reserve")
    public ResponseEntity<String> reserveBook(@PathVariable Long id) {
        Book book = bookRepo.get(id);
        if (book == null || !book.isAvailable()) {
            return ResponseEntity.badRequest().body("Book not available");
        }
        book.setAvailable(false);
        return ResponseEntity.ok("Book ID " + id + " reserved successfully.");
    }

    // ✅ Add new book - LIBRARIAN and ADMIN
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        Long id = idCounter.getAndIncrement();
        book.setId(id);
        book.setAvailable(true);
        bookRepo.put(id, book);
        return ResponseEntity.ok(book);
    }

    // ✅ Update book - LIBRARIAN and ADMIN
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        if (!bookRepo.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        updatedBook.setId(id);
        bookRepo.put(id, updatedBook);
        return ResponseEntity.ok(updatedBook);
    }

    // ✅ Delete book - ADMIN only
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @PostAuthorize("returnObject.statusCodeValue == 200")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        if (!bookRepo.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        bookRepo.remove(id);
        return ResponseEntity.ok("Book ID " + id + " deleted successfully.");
    }
}

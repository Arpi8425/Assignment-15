package com.example.library.model;

public class Reservation {
    private Long id;
    private Long bookId;
    private String username;

    public Reservation() {}

    public Reservation(Long id, Long bookId, String username) {
        this.id = id;
        this.bookId = bookId;
        this.username = username;
    }

    // ✅ Getter and Setter for id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // ✅ Getter and Setter for bookId
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    // ✅ Getter and Setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

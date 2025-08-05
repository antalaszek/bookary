package com.example.bookary.models;

import java.util.Set;

public record BookWithAuthors(Long id, String title, Integer year, Set<Author> authors) {
    public BookWithAuthors {
        authors = authors != null ? Set.copyOf(authors) : Set.of();
    }
}

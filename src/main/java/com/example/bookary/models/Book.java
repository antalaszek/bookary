package com.example.bookary.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.util.Set;

import static java.util.stream.Collectors.toSet;


public record Book(@Id Long id, String title, Integer year,
                   @MappedCollection(idColumn = "BOOK_ID") Set<BookAuthorRef> authors) {
    public Book {
        authors = authors != null ? Set.copyOf(authors) : Set.of();
    }

    public Book(String title, Integer year, Set<Long> authorIds) {
        this(null, title, year, authorIds.stream().map(BookAuthorRef::new).collect(toSet()));
    }
}

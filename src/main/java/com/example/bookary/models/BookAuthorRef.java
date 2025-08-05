package com.example.bookary.models;

import org.springframework.data.relational.core.mapping.Table;

@Table("BOOK_AUTHOR")
public record BookAuthorRef(Long authorId) {
}

package com.example.bookary.models;

import org.springframework.data.annotation.Id;

public record Author(@Id Long id, String name) {
}

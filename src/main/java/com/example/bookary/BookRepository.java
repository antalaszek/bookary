package com.example.bookary;

import com.example.bookary.models.Book;
import org.springframework.data.repository.CrudRepository;

interface BookRepository extends CrudRepository<Book, Long> {
}

package com.example.bookary;

import com.example.bookary.models.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

interface BookRepository extends CrudRepository<Book, Long>, PagingAndSortingRepository<Book, Long> {
}

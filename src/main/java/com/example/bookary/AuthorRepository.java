package com.example.bookary;

import com.example.bookary.models.Author;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Set;

interface AuthorRepository extends CrudRepository<Author, Long>, PagingAndSortingRepository<Author, Long> {
    Integer countAllByIdIn(Set<Long> authorIds);
}

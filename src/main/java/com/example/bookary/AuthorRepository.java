package com.example.bookary;

import com.example.bookary.models.Author;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

interface AuthorRepository extends CrudRepository<Author, Long> {
    Integer countAllByIdIn(Set<Long> authorIds);

}

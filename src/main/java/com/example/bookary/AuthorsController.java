package com.example.bookary;

import com.example.bookary.models.Author;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/authors")
class AuthorsController {
    public record CreateAuthorRequest(String name) {
    }

    private final AuthorRepository authorRepository;

    private AuthorsController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @GetMapping
    private ResponseEntity<Iterable<Author>> findAll(Pageable pageable) {
        var authorsPage = authorRepository.findAll(PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(
                        new Sort.Order(Sort.Direction.ASC, "name")
                ))
        ));
        return ResponseEntity.ok(authorsPage.getContent());
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<Author> getById(@PathVariable Long requestedId) {
        Optional<Author> bookWithAuthors = authorRepository.findById(requestedId);
        return toResponseEntity(bookWithAuthors);
    }

    @PostMapping
    private ResponseEntity<Void> createAuthor(
            @RequestBody CreateAuthorRequest newAuthor,
            UriComponentsBuilder ucb
    ) {
        Author author = authorRepository.save(new Author(newAuthor.name()));
        URI locationOfNewAuthor = ucb
                .path("authors/{id}")
                .buildAndExpand(author.id())
                .toUri();
        return ResponseEntity.created(locationOfNewAuthor).build();
    }


    private static <T> ResponseEntity<T> toResponseEntity(Optional<T> optionalT) {
        return optionalT.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}

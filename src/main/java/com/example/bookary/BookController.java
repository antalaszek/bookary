package com.example.bookary;

import com.example.bookary.models.BookWithAuthors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/books")
class BookController {
    public record CreateBookRequest(String title, Integer year, Set<Long> authorIds) {
    }


    private final BookService bookService;

    private BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<BookWithAuthors> getById(@PathVariable Long requestedId) {
        Optional<BookWithAuthors> bookWithAuthors = bookService.getBookWithAuthors(requestedId);
        return toResponseEntity(bookWithAuthors);
    }

    @PostMapping
    private ResponseEntity<Void> createBook(
            @RequestBody CreateBookRequest newBook,
            UriComponentsBuilder ucb
    ) {
        if (validateNewBook(newBook)) {
            var book = bookService.createBook(newBook.title(), newBook.year(), newBook.authorIds());
            URI locationOfNewBook = ucb
                    .path("books/{id}")
                    .buildAndExpand(book.id())
                    .toUri();
            return ResponseEntity.created(locationOfNewBook).build();
        }
        return ResponseEntity.unprocessableEntity().build();
    }

    private boolean validateNewBook(CreateBookRequest newBook) {
        return newBook.authorIds() != null
                && !newBook.authorIds().isEmpty()
                && bookService.allAuthorsExist(newBook.authorIds())
                && newBook.title() != null
                && newBook.year() != null;
    }


    private static <T> ResponseEntity<T> toResponseEntity(Optional<T> optionalT) {
        return optionalT.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}

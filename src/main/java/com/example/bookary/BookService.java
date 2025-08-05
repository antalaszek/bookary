package com.example.bookary;

import com.example.bookary.models.Book;
import com.example.bookary.models.BookWithAuthors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Transactional
@Service
class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public Book createBook(String title, Integer year, Set<Long> authorIds) {
        var newBook = new Book(title, year, authorIds);
        return bookRepository.save(newBook);
    }

    public boolean allAuthorsExist(Set<Long> authorIds) {
        return authorRepository.countAllByIdIn(authorIds).equals(authorIds.size());
    }

    public Optional<BookWithAuthors> getBookWithAuthors(Long bookId) {
        var book = bookRepository.findById(bookId);
        return book.map(b ->
                new BookWithAuthors(
                        b.id(),
                        b.title(),
                        b.year(),
                        b.authors()
                                .stream()
                                .map(
                                        ref -> authorRepository
                                                .findById(ref.authorId())
                                                .orElseThrow())
                                .collect(toSet())
                ));
    }

    public Page<BookWithAuthors> findAll(PageRequest pageRequest) {
        return bookRepository.findAll(pageRequest).map(book ->
                new BookWithAuthors(
                        book.id(),
                        book.title(),
                        book.year(),
                        book.authors()
                                .stream()
                                .map(
                                        ref -> authorRepository
                                                .findById(ref.authorId())
                                                .orElseThrow())
                                .collect(toSet())
                ));
    }
}

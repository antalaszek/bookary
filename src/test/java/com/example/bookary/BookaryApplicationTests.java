package com.example.bookary;

import com.example.bookary.BookController.CreateBookRequest;
import com.example.bookary.models.BookWithAuthors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Set;

import static com.example.bookary.TestDataProvider.getFirstAuthor;
import static com.example.bookary.TestDataProvider.getSingleBookWithTwoAuthors;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookaryApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnABookWhenDataIsSaved() {
        ResponseEntity<BookWithAuthors> response = restTemplate.getForEntity("/books/13", BookWithAuthors.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        BookWithAuthors responseBook = response.getBody();
        Assertions.assertNotNull(responseBook);
        assertThat(responseBook.authors()).isUnmodifiable();
        assertThat(responseBook).isEqualTo(getSingleBookWithTwoAuthors());
    }

    @Test
    void shouldReturnStatusCode404IfNoSuchBook() {
        ResponseEntity<BookWithAuthors> response = restTemplate.getForEntity("/books/2137", BookWithAuthors.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        BookWithAuthors responseBook = response.getBody();
        assertThat(responseBook).isNull();
    }

    @Test
    void shouldReturnUnprocessableEntityWhenNoSuchAuthor() {
        CreateBookRequest bookRequest = new CreateBookRequest("Leć Adaś", 2023, Set.of(25L));
        ResponseEntity<Void> response = restTemplate.postForEntity("/books", bookRequest, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getHeaders().getLocation()).isNull();
    }

    @Test
    void shouldReturnCreatedForNewBookWithSingleAuthor() {
        CreateBookRequest bookRequest = new CreateBookRequest("Leć Adaś", 2023, Set.of(123L));
        ResponseEntity<Void> response = restTemplate.postForEntity("/books", bookRequest, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI locationOfNewBook = response.getHeaders().getLocation();
        ResponseEntity<BookWithAuthors> newAddedBookResponse = restTemplate.getForEntity(locationOfNewBook, BookWithAuthors.class);
        assertThat(newAddedBookResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        BookWithAuthors bookResponseBody = newAddedBookResponse.getBody();
        Assertions.assertNotNull(bookResponseBody);
        assertThat(bookResponseBody.authors()).isEqualTo(
                Set.of(getFirstAuthor())
        );
        assertThat(bookResponseBody.title()).isEqualTo(
                bookRequest.title()
        );
        assertThat(bookResponseBody.year()).isEqualTo(
                bookRequest.year()
        );


    }

    @Test
    void shouldReturnCreatedForNewBookWithManyAuthors() {
        CreateBookRequest bookRequest = new CreateBookRequest("Leć Adaś", 2023, Set.of(123L, 124L));
        ResponseEntity<Void> response = restTemplate.postForEntity("/books", bookRequest, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void contextLoads() {
    }

}

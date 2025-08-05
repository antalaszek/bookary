package com.example.bookary;

import com.example.bookary.AuthorsController.CreateAuthorRequest;
import com.example.bookary.BookController.CreateBookRequest;
import com.example.bookary.models.Author;
import com.example.bookary.models.BookWithAuthors;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;
import java.util.Set;

import static com.example.bookary.TestDataProvider.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookaryApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnABookWhenDataIsSaved() {
        ResponseEntity<BookWithAuthors> response = restTemplate.getForEntity(
                "/books/13",
                BookWithAuthors.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        BookWithAuthors responseBook = response.getBody();
        Assertions.assertNotNull(responseBook);
        assertThat(responseBook.authors()).isUnmodifiable();
        assertThat(responseBook).isEqualTo(getSingleBookWithOneAuthor());
    }

    @Test
    void shouldReturnStatusCode404IfNoSuchBook() {
        ResponseEntity<BookWithAuthors> response = restTemplate.getForEntity(
                "/books/2137",
                BookWithAuthors.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        BookWithAuthors responseBook = response.getBody();
        assertThat(responseBook).isNull();
    }

    @Test
    @DirtiesContext
    void shouldReturnUnprocessableEntityWhenNoSuchAuthor() {
        CreateBookRequest bookRequest = new CreateBookRequest("Leć Adaś", 2023, Set.of(25L));
        ResponseEntity<Void> response = restTemplate.postForEntity("/books", bookRequest, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getHeaders().getLocation()).isNull();
    }

    @Test
    @DirtiesContext
    void shouldReturnCreatedForNewBookWithSingleAuthor() {
        CreateBookRequest bookRequest = new CreateBookRequest("Leć Adaś", 2023, Set.of(123L));
        ResponseEntity<Void> response = restTemplate.postForEntity("/books", bookRequest, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI locationOfNewBook = response.getHeaders().getLocation();
        ResponseEntity<BookWithAuthors> newAddedBookResponse = restTemplate.getForEntity(
                locationOfNewBook,
                BookWithAuthors.class
        );
        assertThat(newAddedBookResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        BookWithAuthors bookResponseBody = newAddedBookResponse.getBody();
        Assertions.assertNotNull(bookResponseBody);
        assertThat(bookResponseBody.authors()).isEqualTo(Set.of(getFirstAuthor()));
        assertThat(bookResponseBody.title()).isEqualTo(bookRequest.title());
        assertThat(bookResponseBody.year()).isEqualTo(bookRequest.year());


    }

    @Test
    @DirtiesContext
    void shouldReturnCreatedForNewBookWithManyAuthors() {
        CreateBookRequest bookRequest = new CreateBookRequest("Leć Adaś", 2023, Set.of(123L, 124L));
        ResponseEntity<Void> response = restTemplate.postForEntity("/books", bookRequest, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewBook = response.getHeaders().getLocation();
        ResponseEntity<BookWithAuthors> newAddedBookResponse = restTemplate.getForEntity(
                locationOfNewBook,
                BookWithAuthors.class
        );
        assertThat(newAddedBookResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        BookWithAuthors bookResponseBody = newAddedBookResponse.getBody();
        Assertions.assertNotNull(bookResponseBody);
        assertThat(bookResponseBody.authors()).isEqualTo(Set.of(getFirstAuthor(), getSecondAuthor()));
        assertThat(bookResponseBody.title()).isEqualTo(bookRequest.title());
        assertThat(bookResponseBody.year()).isEqualTo(bookRequest.year());
    }

    @Test
    void shouldReturnAnAuthorWhenDataIsSaved() {
        ResponseEntity<Author> response = restTemplate.getForEntity("/authors/123", Author.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Author responseAuthor = response.getBody();
        Assertions.assertNotNull(responseAuthor);
        assertThat(responseAuthor).isEqualTo(getFirstAuthor());
    }

    @Test
    void shouldReturnStatusCode404IfNoSuchAuthor() {
        ResponseEntity<Author> response = restTemplate.getForEntity("/authors/2137", Author.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        Author responseBook = response.getBody();
        assertThat(responseBook).isNull();
    }


    @Test
    @DirtiesContext
    void shouldReturnCreatedForNewAuthor() {
        CreateAuthorRequest authorRequest = new CreateAuthorRequest("Robert Musil");
        ResponseEntity<Void> response = restTemplate.postForEntity(
                "/authors",
                authorRequest,
                Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI locationOfNewAuthor = response.getHeaders().getLocation();
        ResponseEntity<Author> newAuthorResponse = restTemplate.getForEntity(
                locationOfNewAuthor,
                Author.class
        );
        assertThat(newAuthorResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Author authorResponseBody = newAuthorResponse.getBody();
        Assertions.assertNotNull(authorResponseBody);
        assertThat(authorResponseBody.name()).isEqualTo(authorRequest.name());
    }

    @Test
    @DirtiesContext
    void shouldReturnAllAuthorsWhenListIsRequested() {
        ResponseEntity<String> response = restTemplate.getForEntity("/authors", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        int authorsCount = documentContext.read("$.length()");
        assertThat(authorsCount).isEqualTo(2);

        JSONArray ids = documentContext.read("$.*.id");
        assertThat(ids).containsExactlyInAnyOrder(123, 124);

        JSONArray names = documentContext.read("$.*.name");
        assertThat(names).containsExactlyInAnyOrder("Julius Verne", "Adam Małysz");
    }

    @Test
    void shouldReturnPageOfAuthors() {
        ResponseEntity<String> response = restTemplate.getForEntity("/authors?page=0&size=1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        int pageLength = documentContext.read("$.length()");
        assertThat(pageLength).isEqualTo(1);
    }

    @Test
    void shouldReturnSortedPageOfAuthorsByNameDesc() {
        ResponseEntity<String> response = restTemplate.getForEntity("/authors?page=0&size=1&sort=name,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        int pageLength = documentContext.read("$.length()");
        assertThat(pageLength).isEqualTo(1);

        JSONArray ids = documentContext.read("$.*.id");
        assertThat(ids).containsExactly(124);

        JSONArray names = documentContext.read("$.*.name");
        assertThat(names).containsExactly("Julius Verne");

    }

    @Test
    void shouldReturnSortedPageOfAuthorsByNameAsc() {
        ResponseEntity<String> response = restTemplate.getForEntity("/authors?page=0&size=1&sort=name,asc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        int pageLength = documentContext.read("$.length()");
        assertThat(pageLength).isEqualTo(1);

        JSONArray ids = documentContext.read("$.*.id");
        assertThat(ids).containsExactly(123);

        JSONArray names = documentContext.read("$.*.name");
        assertThat(names).containsExactly("Adam Małysz");

    }

    @Test
    @DirtiesContext
    void shouldReturnPageOfBooks() {
        CreateBookRequest bookRequest = new CreateBookRequest("Leć Adaś", 2023, Set.of(123L, 124L));
        ResponseEntity<Void> addBookResponse = restTemplate.postForEntity("/books", bookRequest, Void.class);
        assertThat(addBookResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> response = restTemplate.getForEntity("/books?page=0&size=1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        int pageLength = documentContext.read("$.length()");
        assertThat(pageLength).isEqualTo(1);

        JSONArray ids = documentContext.read("$.*.id");
        assertThat(ids).containsExactly(13);

        JSONArray names = documentContext.read("$.*.title");
        assertThat(names).containsExactly("Jak zostałem skoczkiem");

        JSONArray authorIds = documentContext.read("$.*.authors.*.id");
        assertThat(authorIds).containsExactly(123);

        JSONArray authorNames = documentContext.read("$.*.authors.*.name");
        assertThat(authorNames).containsExactly("Adam Małysz");
    }

    @Test
    @DirtiesContext
    void shouldReturnSortedPageOfBooksByTitleDesc() {
        // In database there are:
        // "J..." 13
        // "S..." 14
        // The new row will get id 1
        // So ordered desc by title will be 14,1,13
        CreateBookRequest bookRequest = new CreateBookRequest("Leć Adaś", 2023, Set.of(123L));
        ResponseEntity<Void> addBookResponse = restTemplate.postForEntity("/books", bookRequest, Void.class);
        assertThat(addBookResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> response = restTemplate.getForEntity("/books?page=0&size=2&sort=title,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        int pageLength = documentContext.read("$.length()");
        assertThat(pageLength).isEqualTo(2);

        JSONArray ids = documentContext.read("$.*.id");
        assertThat(ids).containsExactly(14, 1);

        JSONArray names = documentContext.read("$.*.title");
        assertThat(names).containsExactly("Skok 20k mil", "Leć Adaś");

        JSONArray authorIds = documentContext.read("$.*.authors.*.id");
        assertThat(authorIds).containsExactlyInAnyOrder(123, 124, 123);

        JSONArray authorNames = documentContext.read("$.*.authors.*.name");
        assertThat(authorNames).containsExactlyInAnyOrder("Adam Małysz", "Julius Verne", "Adam Małysz");
    }
}

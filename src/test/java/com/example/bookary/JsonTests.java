package com.example.bookary;

import com.example.bookary.models.BookWithAuthors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static com.example.bookary.TestDataProvider.getSingleBookWithOneAuthor;
import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
public class JsonTests {
    @Autowired
    private JacksonTester<BookWithAuthors> json;

    @Test
    void bookSerializationTest() throws IOException {
        JsonContent<BookWithAuthors> jsoned = json.write(getSingleBookWithOneAuthor());
        assertThat(jsoned).isStrictlyEqualToJson("single_book.json");

        assertThat(jsoned).hasJsonPathNumberValue("@.id");
        assertThat(jsoned).extractingJsonPathNumberValue("@.id").isEqualTo(13);
        assertThat(jsoned).hasJsonPathStringValue("@.title");
        assertThat(jsoned).extractingJsonPathStringValue("@.title").isEqualTo("Jak zostałem skoczkiem");
        assertThat(jsoned).hasJsonPathNumberValue("@.year");
        assertThat(jsoned).extractingJsonPathNumberValue("@.year").isEqualTo(2005);


        assertThat(jsoned).hasJsonPathArrayValue("@.authors");
        assertThat(jsoned).hasJsonPathNumberValue("@.authors[0].id");
        assertThat(jsoned).extractingJsonPathNumberValue("@.authors[0].id").isEqualTo(123);
        assertThat(jsoned).hasJsonPathStringValue("@.authors[0].name");
        assertThat(jsoned).extractingJsonPathStringValue("@.authors[0].name").isEqualTo("Adam Małysz");

    }

    @Test
    void bookDeserializationTest() throws IOException {
        String expected = """
                {
                  "id" : 13,
                  "title" : "Jak zostałem skoczkiem",
                  "year" : 2005,
                  "authors" : [ {
                    "id" : 123,
                    "name" : "Adam Małysz"
                  } ]
                }
                """;
        assertThat(json.parse(expected)).isEqualTo(getSingleBookWithOneAuthor());
    }
}

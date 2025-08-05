package com.example.bookary;

import com.example.bookary.models.Author;
import com.example.bookary.models.BookWithAuthors;

import java.util.Set;

class TestDataProvider {
    private static final Author firstAuthor = new Author(123L, "Adam Małysz");
    private static final Author secondAuthor = new Author(124L, "Julius Verne");
    private static final BookWithAuthors singleBookWithOneAuthor = new BookWithAuthors(13L, "Jak zostałem skoczkiem", 2005, Set.of(firstAuthor));
    private static final BookWithAuthors singleBookWithTwoAuthors = new BookWithAuthors(13L, "Jak zostałem skoczkiem", 2005, Set.of(firstAuthor, secondAuthor));

    public static BookWithAuthors getSingleBookWithOneAuthor() {
        return singleBookWithOneAuthor;
    }

    public static BookWithAuthors getSingleBookWithTwoAuthors() {
        return singleBookWithTwoAuthors;
    }

    public static Author getFirstAuthor() {
        return firstAuthor;
    }

    public static Author getSecondAuthor() {
        return secondAuthor;
    }

}

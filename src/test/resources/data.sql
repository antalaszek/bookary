INSERT INTO author(ID, NAME)
VALUES (123, 'Adam Małysz');
INSERT INTO author(ID, NAME)
VALUES (124, 'Julius Verne');
INSERT INTO book(ID, TITLE, "YEAR")
VALUES (13, 'Jak zostałem skoczkiem', 2005);
INSERT INTO book_author(BOOK_ID, AUTHOR_ID)
VALUES (13, 123);
INSERT INTO book_author(BOOK_ID, AUTHOR_ID)
VALUES (13, 124);

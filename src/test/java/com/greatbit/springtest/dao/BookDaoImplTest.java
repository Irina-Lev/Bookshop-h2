package com.greatbit.springtest.dao;

import com.greatbit.dao.BookDao;
import com.greatbit.model.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest(
        properties = {"jdbcUrl=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1"}
)
public class BookDaoImplTest {
    @Autowired
    private BookDao bookDao;

    @BeforeEach
    public void beforeEach() {
        bookDao.deleteAll();
    }

    @Test
    public void contextCreated() {
    }

    @Test
    public void saveSavesDataToDbAndReturnsEntityWithId() {
        Book book = bookDao.save(new Book("Book name", "book author", 1));
        assertThat(book.getId()).isNotBlank();
        Assertions.assertThat(bookDao.findAll()).extracting("id")
                .containsExactly(book.getId()); //Список книг содержит только добавленную Id
    }

    @Test
    void deleteAllDeleteAllData() {
        bookDao.save(new Book("Book name", "book author", 1));
        Assertions.assertThat(bookDao.findAll()).isNotEmpty();
        bookDao.deleteAll();
        Assertions.assertThat(bookDao.findAll()).isEmpty();
    }

    @Test
    void findAllReturnsAllBooks() {
        Assertions.assertThat(bookDao.findAll()).isEmpty();
        bookDao.save(new Book("Book name", "book author", 1));
        Assertions.assertThat(bookDao.findAll()).isNotEmpty();
    }

    @Test
    void getByIdThrowsRuntimeExceptionIfNoBookFound() {
        assertThatThrownBy(() -> bookDao.getById("1")).isInstanceOf(RuntimeException.class);
    }

    @Test
    void getByIdReturnsCorrectBook() {
        Book book = bookDao.save(new Book("Book name", "book author", 1));
        Book otherBook = bookDao.save(new Book("Other book name", "Other book author", 2));
        Assertions.assertThat(bookDao.getById(book.getId())).isNotNull().extracting("name")
                .isEqualTo(book.getName());
    }

    @Test
    void updateUpdatesDataInDB() {
        Book book = bookDao.save(new Book("Book name", "book author", 1));
        book.setName("New name");
        bookDao.update(book);
        Assertions.assertThat(bookDao.getById(book.getId()).getName()).isEqualTo("New name");
    }

    @Test
    void updateThrowsExceptionOnUpdatingNoSavedBook() {
        assertThatThrownBy(() -> bookDao.update(new Book("Other book name", "Other book author", 2)))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void deleteDeletesCorrectData() {
        Book bookToKeep = bookDao.save(new Book("Book name", "book author", 1));
        Book bookToDelete = bookDao.save(new Book("Other book name", "Other book author", 2));
        bookDao.delete(bookToDelete);
        Assertions.assertThat(bookDao.getById(bookToKeep.getId())).isNotNull();
        assertThatThrownBy(() -> bookDao.getById(bookToDelete.getId())).isInstanceOf(RuntimeException.class);
    }
}

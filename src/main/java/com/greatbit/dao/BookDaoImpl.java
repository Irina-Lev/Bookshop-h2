package com.greatbit.dao;

import com.greatbit.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class BookDaoImpl implements BookDao {
    private final DataSource dataSource;

    @Autowired
    public BookDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Book> findAll() {
        final String selectSql = "SELECT book_id, name, author, pages from book";
        List<Book> books = new ArrayList<>();
        try (
                Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(selectSql);
        ) {
            while (rs.next()) {
                Book book = new Book(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getInt(4));
                books.add(book);
            }
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
        return books;
    }

    @Override
    public Book save(Book book) {
        final String insertSql = "INSERT INTO book (name, author, pages) VALUES (?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, book.getName());
            preparedStatement.setString(2, book.getAuthor());
            preparedStatement.setInt(3, book.getPages());
            preparedStatement.executeUpdate();
            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                rs.next();
                book.setId(rs.getString(1));
            }
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
        return book;
    }

    @Override
    public Book getById(String bookId) {
        String getByIdSql = "SELECT book_id, name, author, pages FROM book WHERE book_id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getByIdSql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, Integer.parseInt(bookId));
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new RuntimeException(String.format("book with id %s was not found", bookId));
                }
                return new Book(rs.getString(1), rs.getString(2),
                        rs.getString(3), rs.getInt(4));
            }
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }

    @Override
    public Book update(Book book) {
        if (Objects.isNull(book.getId())) {
            throw new RuntimeException("Can't update unsaved book");
        }

        String updateSql = "UPDATE book SET name = ?, author = ?, pages = ? WHERE book_id = ? ";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
            preparedStatement.setString(1, book.getName());
            preparedStatement.setString(2, book.getAuthor());
            preparedStatement.setInt(3, book.getPages());
            preparedStatement.setString(4, book.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
        return book;
    }

    @Override
    public void delete(Book book) {
        if (Objects.isNull(book.getId())) { //Удалить не сохранённую книгу нельзя.
            throw new RuntimeException("Can't update unsaved book");
        }
        String deleteByIdSql = "DELETE FROM book WHERE book_id = ?";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(deleteByIdSql)){
            statement.setString(1, book.getId());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }

    @Override
    public void deleteAll() {
        String deleteSql = "TRUNCATE TABLE book";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(deleteSql);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }
}

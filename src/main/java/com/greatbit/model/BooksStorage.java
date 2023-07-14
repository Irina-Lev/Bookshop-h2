package com.greatbit.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BooksStorage {
    private  static List <Book> books = new ArrayList<>();
    static {
        books.add(new Book(
                UUID.randomUUID().toString(),
                "Учение Дона Хуана",
                "Карлос Кастанеда",
                344));
        books.add(new Book(
                UUID.randomUUID().toString(),
                "Богатый папа, бедный папа",
                "Роберт Киосаки",
                401));
    }
    public  static List <Book> getBooks () {
        return books;
    }
}


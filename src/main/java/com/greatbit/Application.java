package com.greatbit;

import com.greatbit.model.Book;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@SpringBootApplication
public class Application {
@Bean
public DataSource h2DataSource (@Value("${jdbcUrl}") String jdbcUrl) {
  JdbcDataSource DataSource =  new JdbcDataSource();
  DataSource.setUrl(jdbcUrl);
  DataSource.setUser("user");
  DataSource.setPassword("password");
  return DataSource;
}

@Bean
public CommandLineRunner cmd (DataSource dataSource){
 return args -> {
  try (InputStream inputStream = this.getClass().getResourceAsStream("/initial.sql")) {
      String sql = new String((inputStream.readAllBytes()));
      try(
              Connection connection = dataSource.getConnection();
              Statement statement = connection.createStatement()
              ) {
          statement.executeUpdate(sql);
          String insertSql = "INSERT INTO book (name, author, pages) VALUES (?,?,?)";
          try(PreparedStatement preparedStatement= connection.prepareStatement(insertSql)){
              preparedStatement.setString(1, "java book");
              preparedStatement.setString(2, "Product Star");
              preparedStatement.setInt(3, 123);
              preparedStatement.executeUpdate();
          }


          System.out.println("Printing books from db...");
          ResultSet resultSet =  statement.executeQuery("SELECT book_id, name, author, pages from book");
          while (resultSet.next()) {
              Book book = new Book(resultSet.getString(1), resultSet.getString(2),
                      resultSet.getString(3), resultSet.getInt(4));
              System.out.println(book);
          }

      }
  }
 };
}


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
//Выводит список бинов на экран.
//    @Bean
//    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//        return args -> {
//
//            System.out.println("Let's inspect the beans provided by Spring Boot:");
//
//            String[] beanNames = ctx.getBeanDefinitionNames();
//            Arrays.sort(beanNames);
//            for (String beanName : beanNames) {
//                System.out.println(beanName);
//            }
//
//        };
//    }

}
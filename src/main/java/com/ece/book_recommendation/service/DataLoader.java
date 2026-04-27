package com.ece.book_recommendation.service;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ece.book_recommendation.model.Book;
import com.ece.book_recommendation.repository.BookRepository;
@Configuration
public class DataLoader {
    @Bean
    CommandLineRunner loadData(BookRepository bookRepository) {
        return args -> {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            getClass().getResourceAsStream("/data/Books.csv")
                    )
            );

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {

                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(";");

                if (parts.length < 6) continue;

                try {
                    String isbn = parts[0].replace("\"", "");
                    String title = parts[1].replace("\"", "");
                    String author = parts[2].replace("\"", "");
                    Integer year = Integer.parseInt(parts[3].replace("\"", ""));
                    String publisher = parts[4].replace("\"", "");
                    String imageUrl = parts[5].replace("\"", "");

                    Book book = new Book(isbn, title, author, year, publisher, imageUrl);

                    bookRepository.save(book);

                } catch (Exception e) {
                    // hatalı satırları skip
                }
            }

            System.out.println("DATA LOADED!");
        };
    }
}

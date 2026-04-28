package com.ece.book_recommendation.service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
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

            if (bookRepository.count() > 0) {
                System.out.println("Books already loaded.");
                return;
            }

            Reader reader = new InputStreamReader(
                    getClass().getResourceAsStream("/data/Book.csv"),
                    StandardCharsets.UTF_8
            );

            CSVParser csvParser = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withTrim()
                    .parse(reader);

            for (CSVRecord record : csvParser) {
                try {
                    Book book = new Book(
                            record.get("book_id"),
                            record.get("title"),
                            record.get("authors"),
                            record.get("publisher"),
                            record.get("published_date"),
                            record.get("description"),
                            parseInteger(record.get("page_count")),
                            record.get("categories"),
                            parseDouble(record.get("average_rating")),
                            parseInteger(record.get("ratings_count")),
                            record.get("language"),
                            record.get("thumbnail"),
                            record.get("search_category")
                    );

                    bookRepository.save(book);

                } catch (Exception e) {
                    System.out.println("Skipped row: " + record.getRecordNumber());
                }
            }

            System.out.println("New book dataset loaded!");
        };
    }

    private Integer parseInteger(String value) {
        try {
            if (value == null || value.isBlank()) return null;
            return Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        try {
            if (value == null || value.isBlank()) return null;
            return Double.parseDouble(value);
        } catch (Exception e) {
            return null;
        }
    }
}

package com.ece.book_recommendation.service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ece.book_recommendation.model.Book;
import com.ece.book_recommendation.repository.BookRepository;
import com.ece.book_recommendation.repository.FavoriteRepository;

@Configuration
public class DataLoader {

    private static final String DATASET_PATH = "/data/book_dataset.csv";
    private static final String FIRST_DATASET_BOOK_ID = "0593135202";

    @Bean
    CommandLineRunner loadData(BookRepository bookRepository, FavoriteRepository favoriteRepository) {
        return args -> {
            InputStream datasetStream = getClass().getResourceAsStream(DATASET_PATH);

            if (datasetStream == null) {
                throw new IllegalStateException("Dataset not found: " + DATASET_PATH);
            }

            if (bookRepository.existsByIsbnAndTitleCompleteIsNotNull(FIRST_DATASET_BOOK_ID)) {
                System.out.println("Book dataset already loaded.");
                return;
            }

            if (bookRepository.count() > 0) {
                favoriteRepository.deleteAll();
                bookRepository.deleteAll();
            }

            Map<String, Book> books = new LinkedHashMap<>();
            int skippedCount = 0;

            try (Reader reader = new InputStreamReader(datasetStream, StandardCharsets.UTF_8);
                    CSVParser csvParser = CSVFormat.DEFAULT
                            .withFirstRecordAsHeader()
                            .withTrim()
                            .parse(reader)) {

                for (CSVRecord record : csvParser) {
                    try {
                        Book book = new Book(
                                firstNonBlank(record, "isbn", "title"),
                                get(record, "title"),
                                get(record, "titleComplete"),
                                get(record, "description"),
                                cleanListValue(get(record, "genres")),
                                get(record, "publisher"),
                                cleanListValue(get(record, "author")),
                                cleanListValue(get(record, "characters")),
                                cleanListValue(get(record, "places")),
                                get(record, "ratingHistogram"),
                                parseAverageRating(get(record, "ratingHistogram")),
                                parseInteger(get(record, "ratingsCount")),
                                parseInteger(get(record, "reviewsCount")),
                                parseInteger(get(record, "numPages")),
                                get(record, "language")
                        );

                        books.put(book.getIsbn(), book);
                    } catch (Exception e) {
                        skippedCount++;
                        System.out.println("Skipped row " + record.getRecordNumber() + ": " + e.getMessage());
                    }
                }
            }

            bookRepository.saveAll(books.values());

            System.out.println("Book dataset loaded. Loaded: " + books.size() + ", skipped: " + skippedCount);
        };
    }

    private String get(CSVRecord record, String column) {
        if (!record.isMapped(column)) {
            return null;
        }

        String value = record.get(column);
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String firstNonBlank(CSVRecord record, String... columns) {
        for (String column : columns) {
            String value = get(record, column);
            if (value != null) {
                return value;
            }
        }

        throw new IllegalArgumentException("No usable book id found");
    }

    private String cleanListValue(String value) {
        if (value == null) {
            return null;
        }

        return value.replace("[", "")
                .replace("]", "")
                .replace("'", "")
                .trim();
    }

    private Integer parseInteger(String value) {
        try {
            if (value == null || value.isBlank()) {
                return null;
            }

            return (int) Double.parseDouble(value);
        } catch (Exception e) {
            return null;
        }
    }

    private Double parseAverageRating(String ratingHistogram) {
        if (ratingHistogram == null || ratingHistogram.isBlank()) {
            return null;
        }

        String cleanedValue = ratingHistogram.replace("[", "").replace("]", "").trim();
        String[] parts = cleanedValue.split(",");

        if (parts.length != 5) {
            return null;
        }

        double weightedTotal = 0;
        double ratingTotal = 0;

        for (int i = 0; i < parts.length; i++) {
            double count = Double.parseDouble(parts[i].trim());
            weightedTotal += count * (i + 1);
            ratingTotal += count;
        }

        if (ratingTotal == 0) {
            return null;
        }

        return weightedTotal / ratingTotal;
    }
}

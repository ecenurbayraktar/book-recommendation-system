package com.ece.book_recommendation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.ece.book_recommendation.model.Book;

@Service
public class RecommenderClient {

    private final RestClient restClient;

    public RecommenderClient(@Value("${recommender.api.url}") String recommenderApiUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(recommenderApiUrl)
                .build();
    }

    public List<String> recommend(List<String> favoriteGenres, List<Book> candidates, int topN) {
        RecommendationResponse response = restClient.post()
                .uri("/recommend")
                .body(new RecommendationRequest(
                        favoriteGenres,
                        candidates.stream()
                                .map(CandidateBook::from)
                                .toList(),
                        topN
                ))
                .retrieve()
                .body(RecommendationResponse.class);

        if (response == null || response.recommendations() == null) {
            return List.of();
        }

        return response.recommendations().stream()
                .map(RecommendationItem::bookId)
                .toList();
    }

    private record RecommendationRequest(
            List<String> favoriteGenres,
            List<CandidateBook> candidates,
            int topN
    ) {
    }

    private record CandidateBook(
            String bookId,
            String title,
            String genres,
            String author,
            Integer ratingsCount
    ) {
        private static CandidateBook from(Book book) {
            return new CandidateBook(
                    book.getBookId(),
                    book.getTitle(),
                    book.getGenres(),
                    book.getAuthor(),
                    book.getRatingsCount()
            );
        }
    }

    private record RecommendationResponse(List<RecommendationItem> recommendations) {
    }

    private record RecommendationItem(String bookId, Double score) {
    }
}

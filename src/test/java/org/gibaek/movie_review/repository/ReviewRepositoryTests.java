package org.gibaek.movie_review.repository;

import org.gibaek.mreview.entity.Member;
import org.gibaek.mreview.entity.Movie;
import org.gibaek.mreview.entity.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
public class ReviewRepositoryTests {

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    public void insertMovieReviews() {
        IntStream.rangeClosed(1, 200).forEach(i -> {
            Long mno = (long) (Math.random() * 100) + 1;

            Long mid = (long) (Math.random() * 100) + 1;
            Member member = Member.builder()
                    .mid(mid)
                    .build();

            Review review = Review.builder()
                    .member(member)
                    .movie(Movie.builder().mno(mno).build())
                    .grade((int) (Math.random() * 5) + 1)
                    .text("이 영화에 대한 느낌..." + i)
                    .build();

            reviewRepository.save(review);
        });
    }

    @Test
    public void testGetMovieReviews() {
        Movie movie = Movie.builder().mno(1L).build();

        List<Review> result = reviewRepository.findByMovie(movie);

        result.forEach(review -> {
            System.out.println(review.getRno());
            System.out.println(review.getGrade());
            System.out.println(review.getText());
            System.out.println(review.getMember().getEmail());
            System.out.println("-----------------------------");
        });
    }
}

package org.gibaek.movie_review.repository;

import org.gibaek.movie_review.entity.MovieImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieImageRepository extends JpaRepository<MovieImage, Long> {
}

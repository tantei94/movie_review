package org.gibaek.movie_review.repository;

import org.gibaek.movie_review.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}

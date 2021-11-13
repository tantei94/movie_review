package org.gibaek.movie_review.repository;

import org.gibaek.movie_review.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("select m, mi, avg(coalesce(r.grade,0)), count(distinct r) " +
            "from Movie m " +
            "left outer join MovieImage mi on mi.movie = m " +
            "left outer join Review r on r.movie = m group by m ")
    Page<Object[]> getListPage(Pageable pageable);

    @Query("select m, mi, count(r) from Movie m left join MovieImage mi on mi.movie = m " +
            "and mi.ino = (select max(mi2.ino) from MovieImage mi2 where mi2.movie = m) " +
            "left join Review r on r.movie = m group by m")
    Page<Object[]> getListPageLastImage(Pageable pageable);

    @Query("select m, mi, avg(coalesce(r.grade, 0)), count(r)" +
            "from Movie m left join MovieImage mi on mi.movie = m " +
            "left join Review r on r.movie =m "+
            "where m.mno =:mno group by mi")
    List<Object[]> getMoviesWithAll(Long mno);

}

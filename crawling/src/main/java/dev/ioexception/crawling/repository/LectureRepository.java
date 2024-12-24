package dev.ioexception.crawling.repository;

import dev.ioexception.crawling.dto.response.LectureYearPriceDBResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import dev.ioexception.crawling.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
    Optional<Lecture> findLectureByLectureId(String lectureId);
    Optional<Lecture> findByLectureIdAndDate(String lectureId, LocalDate now);
    List<Lecture> findAllByTitleContainingAndDate(String title, LocalDate localDate);
    List<Lecture> findAllByDate(LocalDate localDate);

    @Query(value = "SELECT DATE_FORMAT(date, '%Y-%m') AS month, MIN(sale_Price) AS min_price " +
            "FROM lecture " +
            "WHERE lecture_Id = :lectureId AND " +
            "date >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR) " +
            "GROUP BY DATE_FORMAT(date, '%Y-%m')", nativeQuery = true)
    List<Object[]> findMonthlyMinPrices(@Param("lectureId") String lectureId);
}

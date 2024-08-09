package dev.ioexception.crawling.dto.response;

import dev.ioexception.crawling.entity.Lecture;
import dev.ioexception.crawling.entity.LectureDocument;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchedLectureResponse {
    String lectureId;
    String title;
    String instructor;
    String companyName;
    int ordinaryPrice;
    int salePrice;
    String salePercent;
    String siteLink;
    LocalDate dateTime;

    public SearchedLectureResponse toDto(Lecture lecture) {
        return new SearchedLectureResponse(lecture.getLectureId(), lecture.getTitle(), lecture.getInstructor(),
                lecture.getCompanyName(),
                lecture.getOrdinaryPrice(), lecture.getSalePrice(), lecture.getSalePercent(), lecture.getSiteLink(),
                lecture.getDate());
    }

    public static SearchedLectureResponse toDto(LectureDocument lecture) {
        return new SearchedLectureResponse(lecture.getLectureId(), lecture.getTitle(), lecture.getInstructor(),
                lecture.getCompanyName(),
                lecture.getOrdinaryPrice(), lecture.getSalePrice(), lecture.getSalePercent(), lecture.getSiteLink(),
                lecture.getDate());
    }
}

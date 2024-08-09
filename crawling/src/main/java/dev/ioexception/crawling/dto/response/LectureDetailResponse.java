package dev.ioexception.crawling.dto.response;

import dev.ioexception.crawling.entity.LectureDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureDetailResponse {
    private String lectureId;
    private String title;
    private String instructor;
    private String companyName;
    private int ordinaryPrice;
    private int salePrice;
    private String salePercent;
    private String siteLink;
    private String imageLink;

    public static LectureDetailResponse toDto(LectureDocument lectureDocument) {
        return new LectureDetailResponse(
                lectureDocument.getLectureId(),
                lectureDocument.getTitle(),
                lectureDocument.getInstructor(),
                lectureDocument.getCompanyName(),
                lectureDocument.getOrdinaryPrice(),
                lectureDocument.getSalePrice(),
                lectureDocument.getSalePercent(),
                lectureDocument.getSiteLink(),
                lectureDocument.getImageLink()
        );
    }
}

package dev.ioexception.crawling.dto.response;

import dev.ioexception.crawling.entity.LectureDocument;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureDocumentRequest {
    private String id;
    private String title;
    private String instructor;
    private String companyName;
    private int ordinaryPrice;
    private int salePrice;
    private String salePercent;
    private String siteLink;
    private String imageLink;
    private List<String> tag;
    private LocalDate time;

    public LectureDocument toEntity(LectureDocumentRequest request) {
        return LectureDocument.builder()
                .lectureId(request.id)
                .title(request.title)
                .instructor(request.instructor)
                .companyName(request.companyName)
                .ordinaryPrice(request.ordinaryPrice)
                .salePrice(request.salePrice)
                .salePercent(request.salePercent)
                .siteLink(request.siteLink)
                .imageLink(request.imageLink)
                .tag(tag)
                .date(request.time)
                .build();
    }
}

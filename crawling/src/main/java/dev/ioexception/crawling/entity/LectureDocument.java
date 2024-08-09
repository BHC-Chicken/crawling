package dev.ioexception.crawling.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LectureDocument {
    private String lectureId;
    private String title;
    private String instructor;
    private String companyName;
    private int ordinaryPrice;
    private int salePrice;
    private String salePercent;
    private String siteLink;
    private String imageLink;
    private List<String> tag;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
}

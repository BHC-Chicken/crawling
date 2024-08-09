package dev.ioexception.crawling.dto.response;

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
public class LectureMonthPriceResponse {
    private LocalDate data;
    private int price;

    public static LectureMonthPriceResponse toDto(LectureDocument lectureDocument) {
        return new LectureMonthPriceResponse(lectureDocument.getDate(), lectureDocument.getSalePrice());
    }
}

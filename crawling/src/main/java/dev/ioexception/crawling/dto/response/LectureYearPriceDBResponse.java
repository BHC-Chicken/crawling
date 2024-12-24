package dev.ioexception.crawling.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LectureYearPriceDBResponse {
    private String month;
    private Integer minPrice;
}

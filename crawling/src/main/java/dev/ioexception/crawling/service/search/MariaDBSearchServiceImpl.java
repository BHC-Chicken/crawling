package dev.ioexception.crawling.service.search;

import dev.ioexception.crawling.dto.response.LectureYearPriceDBResponse;
import dev.ioexception.crawling.dto.response.LectureYearPriceResponse;
import dev.ioexception.crawling.dto.response.SearchedLectureResponse;
import dev.ioexception.crawling.entity.Lecture;
import dev.ioexception.crawling.repository.LectureRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MariaDBSearchServiceImpl implements SearchService {
    private final LectureRepository lectureRepository;

    @Override
    public List<SearchedLectureResponse> search(String q, String f) {
        List<Lecture> lectures = lectureRepository.findAllByTitleContainingAndDate(q, LocalDate.of(2023, 11, 2));

        if (lectures.isEmpty()) {
            return new ArrayList<>();
        }

        return lectures.stream()
                .map(lecture -> new SearchedLectureResponse().toDto(lecture))
                .collect(Collectors.toList());
    }

    public List<LectureYearPriceDBResponse> lecturePriceYearAgg(String lectureId) {
        List<Object[]> results = lectureRepository.findMonthlyMinPrices(lectureId);

        return results.stream()
                .map(result -> new LectureYearPriceDBResponse(
                        (String) result[0],  // month
                        (Integer) result[1]   // minPrice
                ))
                .collect(Collectors.toList());
    }
}

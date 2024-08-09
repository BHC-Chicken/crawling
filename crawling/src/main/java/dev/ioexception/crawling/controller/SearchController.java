package dev.ioexception.crawling.controller;

import dev.ioexception.crawling.dto.response.LectureDetailResponse;
import dev.ioexception.crawling.dto.response.LectureMonthPriceResponse;
import dev.ioexception.crawling.dto.response.LectureYearPriceResponse;
import dev.ioexception.crawling.dto.response.SearchedLectureResponse;
import dev.ioexception.crawling.service.search.ElasticSearchServiceImpl;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {
    private final ElasticSearchServiceImpl searchService;

    public SearchController(ElasticSearchServiceImpl service) {
        this.searchService = service;
    }

    @GetMapping("/search")
    public ResponseEntity<List<SearchedLectureResponse>> search(@RequestParam String q) throws IOException {
        List<SearchedLectureResponse> result = searchService.search(q);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/lecture/{lectureId}")
    public ResponseEntity<LectureDetailResponse> lectureDetail(@PathVariable String lectureId) throws IOException {
        try {
            LectureDetailResponse lectureDetailResponse = searchService.lectureDetail(lectureId);

            return ResponseEntity.ok(lectureDetailResponse);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<SearchedLectureResponse>> lectureList(HttpSession session) throws IOException {
        List<SearchedLectureResponse> list = searchService.lectureList(session);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/year-agg")
    public ResponseEntity<List<LectureYearPriceResponse>> yearAgg(@RequestParam String q) throws IOException {
        return ResponseEntity.ok(searchService.lecturePriceYearAgg(q));
    }

    @GetMapping("/month-agg")
    public ResponseEntity<List<LectureMonthPriceResponse>> monthAgg(@RequestParam String q,
                                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
            throws IOException {
        return ResponseEntity.ok(searchService.lecturePriceMonthAgg(q, date));
    }
}

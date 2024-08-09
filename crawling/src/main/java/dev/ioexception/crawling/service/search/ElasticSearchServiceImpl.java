package dev.ioexception.crawling.service.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval;
import co.elastic.clients.elasticsearch._types.aggregations.DateHistogramBucket;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import dev.ioexception.crawling.dto.response.LectureDetailResponse;
import dev.ioexception.crawling.dto.response.LectureMonthPriceResponse;
import dev.ioexception.crawling.dto.response.LectureYearPriceResponse;
import dev.ioexception.crawling.dto.response.SearchedLectureResponse;
import dev.ioexception.crawling.entity.LectureDocument;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticSearchServiceImpl implements SearchService {

    private final ElasticsearchClient client;

    @Override
    public List<SearchedLectureResponse> search(String q) throws IOException {
        long start = System.currentTimeMillis();

        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("lecture")
                .query(query -> query
                        .match(m -> m
                                .field("title")
                                .query(q))));

        SearchResponse<LectureDocument> lectures = client.search(searchRequest, LectureDocument.class);

        long end = System.currentTimeMillis();

        log.info("ES_METRIC query.requests=1, query.question={}, query.execution.time={}, timestamp={}", q, end - start,
                System.currentTimeMillis());

        return lectures.hits().hits().stream()
                .map(Hit::source).filter(Objects::nonNull)
                .map(SearchedLectureResponse::toDto)
                .collect(Collectors.toList());
    }

    public LectureDetailResponse lectureDetail(String lectureId) throws IOException {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("lecture")
                .query(q -> q
                        .bool(b -> b
                                .filter(f -> f
                                        .term(t -> t
                                                .field("lectureId")
                                                .value(lectureId)))
                                .filter(f -> f
                                        .term(t -> t
                                                .field("date")
                                                .value(today))))
                ));

        SearchResponse<LectureDocument> searchResponse = client.search(searchRequest, LectureDocument.class);

        if (searchResponse.hits().hits().get(0).source() == null) {
            throw new IllegalArgumentException();
        }

        return LectureDetailResponse.toDto(searchResponse.hits().hits().get(0).source());
    }

    public List<SearchedLectureResponse> lectureList(HttpSession session) throws IOException {
        SearchRequest searchRequest;
        List<FieldValue> searchAfter = getSearchAfterFromSession(session);

        if (searchAfter == null) {
            searchRequest = searchRequestInit();
        } else {
            searchRequest = searchRequestWithSearchAfter(searchAfter);
        }

        SearchResponse<LectureDocument> lectures = client.search(searchRequest, LectureDocument.class);

        List<FieldValue> newSearchAfter = getLastHitSortValues(lectures);
        session.setAttribute("searchAfter", newSearchAfter);

        return lectures.hits().hits().stream()
                .map(Hit::source).filter(Objects::nonNull)
                .map(SearchedLectureResponse::toDto)
                .collect(Collectors.toList());
    }

    public List<LectureYearPriceResponse> lecturePriceYearAgg(String lectureId) throws IOException {
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("lecture")
                .size(0)
                .query(q -> q
                        .term(t -> t
                                .field("lectureId")
                                .value(lectureId)))
                .postFilter(f -> f
                        .range(r -> r
                                .field("date")
                                .from("now-1y/d")
                                .to("now")))
                .aggregations("date-range-aggs", Aggregation.of(a -> a
                        .dateHistogram(dh -> dh
                                .field("date")
                                .calendarInterval(CalendarInterval.Month))
                        .aggregations("min_price", Aggregation.of(aa -> aa
                                .min(m -> m.field("salePrice")))))));

        List<LectureYearPriceResponse> list = new ArrayList<>();
        SearchResponse<LectureDocument> response = client.search(searchRequest, LectureDocument.class);

        for (DateHistogramBucket s : response.aggregations().get("date-range-aggs").dateHistogram().buckets().array()) {
            list.add(new LectureYearPriceResponse(LocalDate.parse(s.keyAsString()),
                    (int) s.aggregations().get("min_price").min().value()));
        }

        return list;
    }

    public List<LectureMonthPriceResponse> lecturePriceMonthAgg(String lectureId, LocalDate localDate)
            throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        YearMonth yearMonth = YearMonth.of(localDate.getYear(), localDate.getMonth());

        String startDate = yearMonth.atDay(1).format(formatter);
        String endDate = yearMonth.atEndOfMonth().format(formatter);

        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("lecture")
                .size(31)
                .query(q -> q
                        .term(t -> t
                                .field("lectureId")
                                .value(lectureId)))
                .source(source -> source
                        .filter(sf -> sf
                                .includes("date")
                                .includes("salePrice")))
                .postFilter(f -> f
                        .range(r -> r
                                .field("date")
                                .from(startDate)
                                .to(endDate)))
                .sort(so -> so
                        .field(f -> f.field("date").order(SortOrder.Asc))));

        SearchResponse<LectureDocument> response = client.search(searchRequest, LectureDocument.class);

        return response.hits().hits().stream()
                .map(Hit::source).filter(Objects::nonNull)
                .map(LectureMonthPriceResponse::toDto)
                .collect(Collectors.toList());
    }

    private SearchRequest searchRequestInit() {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        return SearchRequest.of(s -> s
                .index("lecture")
                .size(20)
                .query(q -> q
                        .term(t -> t
                                .field("date")
                                .value(today)))
                .sort(so -> so
                        .field(f -> f.field("ordinaryPrice").order(SortOrder.Asc))
                ).sort(so -> so
                        .field(f -> f.field("lectureId").order(SortOrder.Asc)))
        );
    }

    private SearchRequest searchRequestWithSearchAfter(List<FieldValue> searchAfter) {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        return SearchRequest.of(s -> s
                .index("lecture")
                .size(20)
                .query(q -> q
                        .term(t -> t
                                .field("date")
                                .value(today)))
                .sort(so -> so
                        .field(f -> f.field("ordinaryPrice").order(SortOrder.Asc))
                ).sort(so -> so
                        .field(f -> f.field("lectureId").order(SortOrder.Asc)))
                .searchAfter(searchAfter)
        );
    }

    private List<FieldValue> getSearchAfterFromSession(HttpSession session) {
        Object sessionAttr = session.getAttribute("searchAfter");

        if (sessionAttr instanceof List<?>) {
            return (List<FieldValue>) sessionAttr;
        }

        return null;
    }

    private List<FieldValue> getLastHitSortValues(SearchResponse<LectureDocument> searchResponse) {
        List<Hit<LectureDocument>> hits = searchResponse.hits().hits();
        if (hits.isEmpty()) {
            return null;
        }
        return hits.get(hits.size() - 1).sort();
    }
}
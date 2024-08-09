package dev.ioexception.crawling.service.search;

import dev.ioexception.crawling.dto.response.SearchedLectureResponse;
import java.io.IOException;
import java.util.List;

public interface SearchService {
    List<SearchedLectureResponse> search(String q) throws IOException;
}

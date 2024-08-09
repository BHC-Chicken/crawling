package dev.ioexception.crawling.service;

import co.elastic.clients.elasticsearch._helpers.bulk.BulkIngester;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import dev.ioexception.crawling.entity.Lecture;
import dev.ioexception.crawling.entity.LectureDocument;
import dev.ioexception.crawling.entity.LectureTag;
import dev.ioexception.crawling.repository.LectureRepository;
import dev.ioexception.crawling.repository.LectureTagRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class IndexService {
    private final LectureRepository lectureRepository;
    private final BulkIngester<BulkOperation> ingester;
    private final LectureTagRepository lectureTagRepository;

    public void inputIndexByJavaClient() throws IOException {
        List<LectureDocument> documents = getLectureList();
        log.info("document size: {}", documents.size());

        for (LectureDocument document : documents) {
            BulkOperation operation = BulkOperation.of(op -> op
                    .index(idx -> idx
                            .index("lecture")
                            .document(document)));

            ingester.add(operation);
        }
    }

    public List<String> getTagNamesByLectureId(String lectureId) {
        List<LectureTag> lectureTags = lectureTagRepository.getLectureTagsWithTagByLectureId(lectureId,
                LocalDate.of(2023, 11, 2));

        return lectureTags.stream()
                .map(lt -> lt.getTag().getName())
                .collect(Collectors.toList());
    }

    public List<LectureDocument> getLectureList() {
        List<Lecture> lectures = lectureRepository.findAllByDate(LocalDate.of(2023, 11, 2));
        List<LectureDocument> documents = new ArrayList<>();
        log.info("size : {}", lectures.size());

        for (Lecture lecture : lectures) {
            int num = 0;
            if (lecture.getSalePrice() > 10000) {
                num = lecture.getSalePrice() - 10000;
            } else {
                num = lecture.getSalePrice();
            }
            LectureDocument lectureDocument = LectureDocument.builder()
                    .lectureId(lecture.getLectureId())
                    .title(lecture.getTitle())
                    .instructor(lecture.getInstructor())
                    .companyName(lecture.getCompanyName())
                    .ordinaryPrice(lecture.getOrdinaryPrice())
                    .salePrice(lecture.getSalePrice())
                    .salePercent(lecture.getSalePercent())
                    .siteLink(lecture.getSiteLink())
                    .imageLink(lecture.getImageLink())
                    .tag(getTagNamesByLectureId(lecture.getLectureId()))
                    .date(LocalDate.of(2024, 8, i))
                    .build();

            documents.add(lectureDocument);
        }

        return documents;
    }
}

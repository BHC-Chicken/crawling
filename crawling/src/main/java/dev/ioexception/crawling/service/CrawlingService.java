package dev.ioexception.crawling.service;

import dev.ioexception.crawling.entity.Lecture;

import dev.ioexception.crawling.page.site.ArtandStudyCrawling;
import dev.ioexception.crawling.page.site.GoormCrawling;
import dev.ioexception.crawling.page.site.MegaCrawling;
import dev.ioexception.crawling.page.site.YbmCrawling;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CrawlingService {
    private final MegaCrawling megaCrawling;
    private final GoormCrawling goormCrawling;
    private final YbmCrawling ybmCrawling;
    private final ArtandStudyCrawling artandStudyCrawling;

    public List<Lecture> getMega() throws IOException {

        return megaCrawling.getSaleLecture();
    }

    public List<Lecture> getYbm() throws IOException, InterruptedException {

        return ybmCrawling.getSaleLecture();
    }
    public List<Lecture> getArtandStudy() throws IOException, InterruptedException {

        return artandStudyCrawling.getSaleLecture();
    }

    public List<Lecture> getGoorm() throws IOException {

        return goormCrawling.getSaleLecture();
    }
}

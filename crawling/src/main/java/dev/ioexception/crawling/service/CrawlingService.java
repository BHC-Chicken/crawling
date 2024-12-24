package dev.ioexception.crawling.service;

import dev.ioexception.crawling.page.site.GoormCrawling;
import dev.ioexception.crawling.page.site.InflearnCrawling;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CrawlingService {
    private final GoormCrawling goormCrawling;
    private final InflearnCrawling inflearnCrawling;

    public void getGoorm() throws IOException {

        goormCrawling.getSaleLecture();
    }
  
    public void getInflearn() {
        inflearnCrawling.getLecture();
    }
}

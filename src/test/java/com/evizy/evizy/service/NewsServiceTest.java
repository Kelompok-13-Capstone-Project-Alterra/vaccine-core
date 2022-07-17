package com.evizy.evizy.service;

import com.evizy.evizy.domain.dao.Admin;
import com.evizy.evizy.domain.dao.City;
import com.evizy.evizy.domain.dao.News;
import com.evizy.evizy.domain.dto.AdminsRequest;
import com.evizy.evizy.domain.dto.CityRequest;
import com.evizy.evizy.domain.dto.NewsRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.repository.CityRepository;
import com.evizy.evizy.repository.NewsRepository;
import com.evizy.evizy.repository.VaccineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = NewsService.class)
class NewsServiceTest {
    @MockBean
    private NewsRepository newsRepository;

    @Autowired
    private NewsService newsService;

    @Test
    void createNewsSuccess_Test() {
        when(newsRepository.save(any())).thenAnswer(i -> {
            ((News) i.getArgument(0)).setId(1L);
            return null;
        });

        NewsRequest newsRequest = newsService.create(NewsRequest.builder()
                .title("This is title")
                .description("This is desc")
                .content("This is content")
                .admin(AdminsRequest.builder()
                        .id(1L)
                        .build())
                .build());
        assertEquals(1L, newsRequest.getId());
        assertEquals("This is title", newsRequest.getTitle());
        assertEquals("This is desc", newsRequest.getDescription());
        assertEquals("This is content", newsRequest.getContent());
        assertEquals(1L, newsRequest.getAdmin().getId());
    }

    @Test
    void deleteNewsFail_Test() {
        when(newsRepository.findById(any())).thenReturn(Optional.empty());
        doNothing().when(newsRepository).delete(any());

        try {
            newsService.delete(1L);
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteNewsSuccess_Test() {
        when(newsRepository.findById(any())).thenReturn(Optional.of(News.builder()
                .title("This is title")
                .description("This is desc")
                .content("This is content")
                .admin(Admin.builder()
                        .id(1L)
                        .build())
                .build()));
        doNothing().when(newsRepository).delete(any());
        newsService.delete(1L);
    }

    @Test
    void findAllNewsSuccess_Test() {
        when(newsRepository.findAll()).thenReturn(List.of(
                News.builder()
                        .id(1L)
                        .title("This is title 1")
                        .description("This is desc 1")
                        .content("This is content 1")
                        .admin(Admin.builder()
                                .id(1L)
                                .build())
                        .build(),
                News.builder()
                        .id(2L)
                        .title("This is title 2")
                        .description("This is desc 2")
                        .content("This is content 2")
                        .admin(Admin.builder()
                                .id(2L)
                                .build())
                        .build()
        ));

        List<NewsRequest> news = newsService.find();
        assertEquals(1L, news.get(0).getId());
        assertEquals("This is title 1", news.get(0).getTitle());
        assertEquals("This is desc 1", news.get(0).getDescription());
        assertEquals("This is content 1", news.get(0).getContent());
        assertEquals(1L, news.get(0).getAdmin().getId());
        assertEquals(2L, news.get(1).getId());
        assertEquals("This is title 2", news.get(1).getTitle());
        assertEquals("This is desc 2", news.get(1).getDescription());
        assertEquals("This is content 2", news.get(1).getContent());
        assertEquals(2L, news.get(1).getAdmin().getId());
    }

    @Test
    void findNewsByIdSuccess_Test() {
        when(newsRepository.findById(any())).thenReturn(Optional.of(
                News.builder()
                    .id(1L)
                    .title("This is title")
                    .description("This is desc")
                    .content("This is content")
                    .admin(Admin.builder()
                            .id(1L)
                            .build())
                    .build()
        ));

        NewsRequest news = newsService.find(1L);
        assertEquals(1L, news.getId());
        assertEquals("This is title", news.getTitle());
        assertEquals("This is desc", news.getDescription());
        assertEquals("This is content", news.getContent());
        assertEquals(1L, news.getAdmin().getId());
    }

    @Test
    void findNewsByIdFail_Test() {
        when(newsRepository.findById(any())).thenReturn(Optional.empty());

        try {
            NewsRequest news = newsService.find(1L);
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void updateNewsFail_Test() {
        when(newsRepository.findById(any())).thenReturn(Optional.empty());

        try {
            NewsRequest news = newsService.update(1L, NewsRequest.builder()
                    .title("This is title 1")
                    .description("This is desc 1")
                    .content("This is content 1")
                    .build());
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e){
            fail();
        }
    }

    @Test
    void updateNewsSuccess_Test() {
        when(newsRepository.findById(any())).thenReturn(Optional.of(News.builder()
                .id(1L)
                .title("This is title")
                .description("This is desc")
                .content("This is content")
                .admin(Admin.builder()
                        .id(1L)
                        .build())
                .build()));

        NewsRequest news = newsService.update(1L, NewsRequest.builder()
                .title("This is title 1")
                .description("This is desc 1")
                .content("This is content 1")
                .build());
        assertEquals(1L, news.getId());
        assertEquals("This is title 1", news.getTitle());
        assertEquals("This is desc 1", news.getDescription());
        assertEquals("This is content 1", news.getContent());
        assertEquals(1L, news.getAdmin().getId());
    }
}
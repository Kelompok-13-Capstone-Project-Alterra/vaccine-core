package com.evizy.evizy.service;

import com.evizy.evizy.constant.ResponseMessage;
import com.evizy.evizy.domain.dao.Admin;
import com.evizy.evizy.domain.dao.News;
import com.evizy.evizy.domain.dto.AdminsRequest;
import com.evizy.evizy.domain.dto.NewsRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;

    public NewsRequest create(NewsRequest request) throws BusinessFlowException {
        News news = News.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .content(request.getContent())
                .admin(Admin.builder()
                        .id(request.getAdmin().getId())
                        .build())
                .build();
        newsRepository.save(news);
        return NewsRequest.builder()
                .id(news.getId())
                .title(news.getTitle())
                .description(news.getDescription())
                .content(news.getContent())
                .admin(AdminsRequest.builder()
                        .id(news.getAdmin().getId())
                        .name(news.getAdmin().getName())
                        .build())
                .build();
    }

    public void delete(Long id) throws BusinessFlowException {
        Optional<News> optionalNews = newsRepository.findById(id);
        if (optionalNews.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.NOT_FOUND, "News not found!");
        }

        newsRepository.delete(optionalNews.get());
    }

    public NewsRequest update(Long id, NewsRequest request) throws BusinessFlowException {
        Optional<News> optionalNews = newsRepository.findById(id);
        if (optionalNews.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.NOT_FOUND, "News not found!");
        }

        News news = optionalNews.get();
        news.setTitle(request.getTitle());
        news.setDescription(request.getDescription());
        news.setContent(request.getContent());
        newsRepository.save(news);

        return NewsRequest.builder()
                .id(news.getId())
                .title(news.getTitle())
                .description(news.getDescription())
                .content(news.getContent())
                .admin(AdminsRequest.builder()
                        .id(news.getAdmin().getId())
                        .name(news.getAdmin().getName())
                        .build())
                .build();
    }

    public NewsRequest find(Long id) {
        Optional<News> optionalNews = newsRepository.findById(id);
        if (optionalNews.isEmpty())
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.BAD_REQUEST, "News not found!");

        News news = optionalNews.get();
        return NewsRequest
                .builder()
                .id(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .description(news.getDescription())
                .admin(AdminsRequest.builder()
                        .id(news.getAdmin().getId())
                        .name(news.getAdmin().getName())
                        .build())
                .build();
    }

    public List<NewsRequest> find() {
        List<News> newsList = newsRepository.findAll();
        List<NewsRequest> newsRequests = new ArrayList<>();
        for(News news : newsList) {
            newsRequests.add(NewsRequest
                    .builder()
                    .id(news.getId())
                    .title(news.getTitle())
                    .description(news.getDescription())
                    .content(news.getContent())
                    .admin(AdminsRequest.builder()
                            .id(news.getAdmin().getId())
                            .name(news.getAdmin().getName())
                            .build())
                    .build());
        }
        return newsRequests;
    }
}

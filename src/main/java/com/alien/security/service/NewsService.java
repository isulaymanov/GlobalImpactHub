package com.alien.security.service;

import com.alien.security.entity.Event;
import com.alien.security.entity.News;
import com.alien.security.entity.UserModel;
import com.alien.security.repo.EventRepository;
import com.alien.security.repo.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    public News createNews(News news, UserModel user) {
        news.setUser(user);
        return newsRepository.save(news);
    }

    public List<News> getNewsByUser(UserModel user) {
        return newsRepository.findByUser(user);
    }

    public News NewsById(Long id) {
        return newsRepository.findById(id).orElseThrow(() ->
                new RuntimeException("News not found with id: " + id));
    }

    public Optional<News> getNewsById(Long id) {
        return newsRepository.findById(id);
    }
    public void deleteNews(Long id, UserModel user) {
        News news = newsRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("News not found or not owned by user"));
        newsRepository.delete(news);
    }


    @Transactional
    public News updateNews(Long id, News updatedNews, UserModel user) {
        Optional<News> newsOptional = newsRepository.findByIdAndUser(id, user);
        if (newsOptional.isPresent()) {
            News existingNews = newsOptional.get();

            if (updatedNews.getCategory() != null && !updatedNews.getCategory().equals(existingNews.getCategory())) {
                existingNews.setCategory(updatedNews.getCategory());
            }
            if (updatedNews.getTitle() != null && !updatedNews.getTitle().equals(existingNews.getTitle())) {
                existingNews.setTitle(updatedNews.getTitle());
            }
            if (updatedNews.getPhotoUrl() != null && !updatedNews.getPhotoUrl().equals(existingNews.getPhotoUrl())) {
                existingNews.setPhotoUrl(updatedNews.getPhotoUrl());
            }
            if (updatedNews.getContent() != null && !updatedNews.getContent().equals(existingNews.getContent())) {
                existingNews.setContent(updatedNews.getContent());
            }
            if (updatedNews.getStatus() != null && !updatedNews.getStatus().equals(existingNews.getStatus())) {
                existingNews.setStatus(updatedNews.getStatus());
            }
            if (updatedNews.getCreated_at() != null && !updatedNews.getCreated_at().equals(existingNews.getCreated_at())) {
                existingNews.setCreated_at(updatedNews.getCreated_at());
            }

            if (updatedNews.getUpdated_at() != null && !updatedNews.getUpdated_at().equals(existingNews.getUpdated_at())) {
                existingNews.setUpdated_at(updatedNews.getUpdated_at());
            }

            return newsRepository.save(existingNews);
        }
        return null;
    }


    public String saveNewsPhoto(MultipartFile file) throws IOException {
        String uploadDir = "D:/uploadsNews";
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String filePath = Paths.get(uploadDir, fileName).toString();

        file.transferTo(new File(filePath));

        return "/uploadsNews/" + fileName;
    }

    public List<News> getAllNews() {
        return newsRepository.findAll();
    }


}

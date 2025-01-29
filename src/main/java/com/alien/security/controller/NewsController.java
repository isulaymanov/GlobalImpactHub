package com.alien.security.controller;

import com.alien.security.entity.Event;
import com.alien.security.entity.News;
import com.alien.security.entity.UserModel;
import com.alien.security.service.EventService;
import com.alien.security.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/news")
public class NewsController {
    @Autowired
    private NewsService newsService;

    @PostMapping("/create")
    public News createNews(@RequestBody News news, @AuthenticationPrincipal UserModel user) {
        return newsService.createNews(news, user);
    }

    @GetMapping("/currentuser")
    public ResponseEntity<?> getNewsForCurrentUser(@AuthenticationPrincipal UserModel currentUser) {
        List<News> news = newsService.getNewsByUser(currentUser);

        if (news == null || news.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "У вас нет записей"));
        }

        return ResponseEntity.ok(news);
    }


    @GetMapping("/all")
    public ResponseEntity<List<News>> getAllNews() {
        List<News> news = newsService.getAllNews();

        if (news.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(news);
    }


    @PatchMapping("/edit/{id}")
    public ResponseEntity<String> updateNews(
            @PathVariable("id") Long id,
            @RequestBody News updatedNews,
            @AuthenticationPrincipal UserModel user) {
        try {
            News updated = newsService.updateNews(id, updatedNews, user);
            if (updated != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body("ENews Updated Successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ENews not found or not allowed to update");
            }
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public void deleteNews(@PathVariable Long id, @AuthenticationPrincipal UserModel user) {
        newsService.deleteNews(id, user);
    }


    @PostMapping("/uploadPhoto/{id}")
    public ResponseEntity<?> uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserModel user) {
        try {
            Optional<News> newsOptional = newsService.getNewsById(id);
            if (newsOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            News news = newsOptional.get();
            String photoUrl = newsService.saveNewsPhoto(file);
            news.setPhotoUrl(photoUrl);
            newsService.updateNews(id, news, user);

            return ResponseEntity.ok("Фото загружена: " + photoUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Ошибка загрузки фото");
        }
    }

    @GetMapping("/photo/{id}")
    public ResponseEntity<Resource> getPhoto(@PathVariable Long id) {
        Optional<News> newsOptional = newsService.getNewsById(id);
        if (newsOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        News news = newsOptional.get();
        String photoUrl = news.getPhotoUrl();

        if (photoUrl == null || photoUrl.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path path = Paths.get("D:/uploadsNews").resolve(photoUrl.substring(photoUrl.lastIndexOf("/") + 1));  // Восстанавливаем путь до файла
            Resource resource = new FileSystemResource(path);

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{id}")
    public News NewsById(@PathVariable Long id) {
        return newsService.NewsById(id);
    }

}
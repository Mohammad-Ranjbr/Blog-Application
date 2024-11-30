package com.blog_application.controller;

import com.blog_application.model.notice.Notice;
import com.blog_application.repository.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeRepository noticeRepository;

    @GetMapping("/")
    public ResponseEntity<List<Notice>> getNotices(){
        List<Notice> notices = noticeRepository.findAllActiveNotices();
        if(notices != null){
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(120, TimeUnit.SECONDS))
                    .body(notices);
        } else {
            return null;
        }
    }

}

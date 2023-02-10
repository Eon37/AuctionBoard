package com.example.AuctionBoard.api.notice;

import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

public interface NoticeService {
    Collection<Notice> getAllActive(int pageNo, int pageSize);
    Notice getById(Long id);
    Notice upsert(Notice notice, MultipartFile image);
    void delete(Long id);
    void deactivate(Long id);
}

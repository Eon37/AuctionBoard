package com.example.AuctionBoard.api.image;

import org.springframework.web.multipart.MultipartFile;

public interface DBImageService {
    DBImage save(DBImage image);
    DBImage saveFromMultipart(MultipartFile image);
}

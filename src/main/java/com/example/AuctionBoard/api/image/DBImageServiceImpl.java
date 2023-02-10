package com.example.AuctionBoard.api.image;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DBImageServiceImpl implements DBImageService {
    private final DBImageRepository imageRepository;

    public DBImageServiceImpl(DBImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public DBImage save(DBImage image) {
        return imageRepository.save(image);
    }

    private DBImage fromMultipart(MultipartFile image) {
        try {
            return new DBImage(null, image.getContentType(), image.getBytes());
        } catch (Exception e) {
            //todo log
            return DefaultImages.dbImage();
        }
    }

    @Override
    public DBImage saveFromMultipart(MultipartFile image) {
        return save(fromMultipart(image));
    }
}

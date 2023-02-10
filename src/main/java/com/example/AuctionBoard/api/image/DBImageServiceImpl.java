package com.example.AuctionBoard.api.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DBImageServiceImpl implements DBImageService {
    private static final Logger logger = LoggerFactory.getLogger(DBImageServiceImpl.class);

    private static final DBImage DB_IMAGE = new DBImage(null, "", new byte[0]);

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
            logger.error("Exception while converting image from multipart file", e);
            return DB_IMAGE;
        }
    }

    @Override
    public DBImage saveFromMultipart(MultipartFile image) {
        return save(fromMultipart(image));
    }
}

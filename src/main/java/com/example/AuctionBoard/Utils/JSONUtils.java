package com.example.AuctionBoard.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class JSONUtils {
    private static final Logger logger = LoggerFactory.getLogger(JSONUtils.class);

    public static <T> T fromString(String json, Class<T> type) {
        ObjectMapper objectMapper = new ObjectMapper();

        T result;
        try {
             result = objectMapper.readValue(json, type);
        } catch (Exception e) {
            String message = "Incorrect JSON data";
            logger.error(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }

        return result;
    }
}

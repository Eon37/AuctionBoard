package com.example.AuctionBoard.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class JSONUtils {
    public static <T> T fromString(String json, Class<T> type) {
        ObjectMapper objectMapper = new ObjectMapper();

        T result;
        try {
             result = objectMapper.readValue(json, type);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect notice JSON data");
        }

        return result;
    }
}

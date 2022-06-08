package com.evizy.evizy.util;

import com.evizy.evizy.domain.common.ApiResponse;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class Response {
    public static ResponseEntity<Object> build(String message, HttpStatus httpStatus, Object data) {
        return new ResponseEntity<>(ApiResponse
                .builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                .data(data)
                .build(), httpStatus);
    }
}

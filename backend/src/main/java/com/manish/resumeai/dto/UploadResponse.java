package com.manish.resumeai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {
    
    private String sessionId;
    private String message;
    private boolean success;
    private int resumeLength;
    private String summary;
    
    public static UploadResponse success(String sessionId, String message, int length, String summary) {
        return UploadResponse.builder()
                .sessionId(sessionId)
                .message(message)
                .success(true)
                .resumeLength(length)
                .summary(summary)
                .build();
    }
    
    public static UploadResponse error(String message) {
        return UploadResponse.builder()
                .message(message)
                .success(false)
                .build();
    }
}

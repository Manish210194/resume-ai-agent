package com.manish.resumeai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryResponse {
    
    private String answer;
    private boolean success;
    private String error;
    private String sessionId;
    
    public static QueryResponse success(String answer, String sessionId) {
        return QueryResponse.builder()
                .answer(answer)
                .success(true)
                .sessionId(sessionId)
                .build();
    }
    
    public static QueryResponse error(String error) {
        return QueryResponse.builder()
                .error(error)
                .success(false)
                .build();
    }
}

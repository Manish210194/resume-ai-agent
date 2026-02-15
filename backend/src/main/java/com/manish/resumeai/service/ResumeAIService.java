package com.manish.resumeai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ResumeAIService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    private final Map<String, String> sessionResumes = new ConcurrentHashMap<>();
    
    @Value("${gemini.api.key}")
    private String apiKey;
    
    @Value("${gemini.model:gemini-2.0-flash-exp}")
    private String model;

    public ResumeAIService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public String storeResume(String sessionId, String resumeText) {
        sessionResumes.put(sessionId, resumeText);
        log.info("Stored resume for session: {} (length: {})", sessionId, resumeText.length());
        
        try {
            return generateSummary(resumeText);
        } catch (Exception e) {
            log.error("Error generating summary: ", e);
            return "Resume uploaded successfully. Ready to answer your questions!";
        }
    }

    private String generateSummary(String resumeText) {
        String prompt = "Provide a 2-sentence summary of this resume highlighting the person's role and key strengths:\n\n" + 
                       resumeText.substring(0, Math.min(2000, resumeText.length()));
        
        return callGeminiAPI(prompt, null);
    }

    public String answerQuestion(String sessionId, String question, String additionalContext) {
        String resumeText = sessionResumes.get(sessionId);
        
        if (resumeText == null) {
            return "Please upload your resume first before asking questions.";
        }

        try {
            return callGeminiAPI(question, resumeText, additionalContext);
        } catch (Exception e) {
            log.error("Error answering question: ", e);
            return "I encountered an error processing your question. Please try again.";
        }
    }

    private String callGeminiAPI(String question, String resumeText, String additionalContext) {
        StringBuilder fullPrompt = new StringBuilder();
        
        fullPrompt.append("You are an expert career coach and resume analyzer. ");
        fullPrompt.append("You help job seekers understand their resume, prepare for interviews, ");
        fullPrompt.append("and match their qualifications to job requirements.\n\n");
        
        if (resumeText != null && !resumeText.isEmpty()) {
            fullPrompt.append("Here is the candidate's resume:\n\n");
            fullPrompt.append(resumeText);
            fullPrompt.append("\n\n");
        }
        
        if (additionalContext != null && !additionalContext.isEmpty()) {
            fullPrompt.append("Additional context (e.g., job description):\n\n");
            fullPrompt.append(additionalContext);
            fullPrompt.append("\n\n");
        }
        
        fullPrompt.append("INSTRUCTIONS:\n");
        fullPrompt.append("- Provide specific, actionable answers based on the resume\n");
        fullPrompt.append("- Use concrete examples from the person's experience\n");
        fullPrompt.append("- Be encouraging but honest\n");
        fullPrompt.append("- Keep answers concise (2-3 paragraphs)\n\n");
        
        fullPrompt.append("Question: ").append(question);

        try {
            // Build request body in Gemini format
            Map<String, Object> requestBody = new HashMap<>();
            
            Map<String, Object> part = new HashMap<>();
            part.put("text", fullPrompt.toString());
            
            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(part));
            
            requestBody.put("contents", List.of(content));
            
            // Generation config
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("maxOutputTokens", 2048);
            requestBody.put("generationConfig", generationConfig);
            
            log.info("Calling Gemini API with model: {}", model);
            log.debug("API Key present: {}", apiKey != null && !apiKey.isEmpty());
            log.debug("API Key starts correctly: {}", apiKey != null && apiKey.startsWith("AIza"));
            
            // IMPORTANT: Build URL with API key as query parameter
            String apiUrl = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                model, apiKey
            );
            
            log.debug("Calling URL: https://generativelanguage.googleapis.com/v1beta/models/{}:generateContent?key=<hidden>", model);
            
            String response = webClient.post()
                    .uri(apiUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.debug("Gemini API response received successfully");
            return extractGeminiResponseText(response);
            
        } catch (WebClientResponseException e) {
            log.error("Gemini API error - Status: {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            
            if (e.getStatusCode().value() == 403) {
                log.error("403 FORBIDDEN");
                log.error("This usually means:");
                log.error("1. API not enabled at: https://console.cloud.google.com/apis/library/generativelanguage.googleapis.com");
                log.error("2. API key is invalid or has no access");
                return "API access denied. Please enable the Generative Language API in Google Cloud Console.";
            } else if (e.getStatusCode().value() == 400) {
                log.error("400 BAD REQUEST - Check request format");
                log.error("Error details: {}", e.getResponseBodyAsString());
                return "Invalid request format. Check logs for details.";
            } else if (e.getStatusCode().value() == 404) {
                log.error("404 NOT FOUND - Model '{}' not found", model);
                log.error("Try using: gemini-2.0-flash-exp or gemini-1.5-flash or gemini-1.5-pro");
                return "Model not found. Try: gemini-2.0-flash-exp, gemini-1.5-flash, or gemini-1.5-pro";
            } else if (e.getStatusCode().value() == 429) {
                return "Too many requests. Please wait a moment.";
            }
            
            return "Error " + e.getStatusCode() + ": " + e.getResponseBodyAsString();
        } catch (Exception e) {
            log.error("Unexpected error calling Gemini API: ", e);
            return "Unexpected error: " + e.getMessage();
        }
    }

    private String callGeminiAPI(String prompt, String resumeText) {
        return callGeminiAPI(prompt, resumeText, null);
    }

    private String extractGeminiResponseText(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText();
                }
            }
            
            log.error("Unexpected response structure: {}", jsonResponse);
            return "I couldn't process that response. Please try again.";
            
        } catch (Exception e) {
            log.error("Error parsing Gemini response: ", e);
            return "Error parsing response.";
        }
    }

    public boolean hasResume(String sessionId) {
        return sessionResumes.containsKey(sessionId);
    }

    public void clearSession(String sessionId) {
        sessionResumes.remove(sessionId);
        log.info("Cleared session: {}", sessionId);
    }
}
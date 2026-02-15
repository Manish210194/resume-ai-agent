package com.manish.resumeai.controller;

import com.manish.resumeai.dto.QueryRequest;
import com.manish.resumeai.dto.QueryResponse;
import com.manish.resumeai.dto.UploadResponse;
import com.manish.resumeai.service.ResumeAIService;
import com.manish.resumeai.util.ResumeParser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeAIService aiService;
    private final ResumeParser resumeParser;

    /**
     * Upload resume file (PDF or DOCX)
     */
    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadResume(@RequestParam("file") MultipartFile file) {
        try {
            log.info("Received resume upload: {}", file.getOriginalFilename());
            
            // Validate file
            if (!resumeParser.isValidResumeFile(file)) {
                return ResponseEntity.badRequest().body(
                    UploadResponse.error("Invalid file. Please upload a PDF or DOCX file.")
                );
            }
            
            // Extract text from resume
            String resumeText = resumeParser.extractText(file);
            
            if (resumeText == null || resumeText.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    UploadResponse.error("Could not extract text from the file. Please ensure it's a valid resume.")
                );
            }
            
            // Generate session ID
            String sessionId = UUID.randomUUID().toString();
            
            // Store resume and get summary
            String summary = aiService.storeResume(sessionId, resumeText);
            
            log.info("Resume uploaded successfully. Session: {}", sessionId);
            
            return ResponseEntity.ok(UploadResponse.success(
                sessionId,
                "Resume uploaded successfully!",
                resumeText.length(),
                summary
            ));
            
        } catch (Exception e) {
            log.error("Error uploading resume: ", e);
            return ResponseEntity.internalServerError().body(
                UploadResponse.error("Error processing resume: " + e.getMessage())
            );
        }
    }

    /**
     * Ask questions about the uploaded resume
     */
    @PostMapping("/query")
    public ResponseEntity<QueryResponse> queryResume(
            @Valid @RequestBody QueryRequest request,
            @RequestHeader(value = "X-Session-ID", required = false) String sessionId) {
        
        try {
            log.info("Received query: {} (session: {})", request.getQuestion(), sessionId);
            
            if (sessionId == null || sessionId.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    QueryResponse.error("Session ID required. Please upload your resume first.")
                );
            }
            
            if (!aiService.hasResume(sessionId)) {
                return ResponseEntity.badRequest().body(
                    QueryResponse.error("No resume found for this session. Please upload your resume first.")
                );
            }
            
            String answer = aiService.answerQuestion(
                sessionId, 
                request.getQuestion(), 
                request.getContext()
            );
            
            return ResponseEntity.ok(QueryResponse.success(answer, sessionId));
            
        } catch (Exception e) {
            log.error("Error processing query: ", e);
            return ResponseEntity.ok(QueryResponse.error(
                "I encountered an error processing your question. Please try again."
            ));
        }
    }

    /**
     * Get suggested questions based on common interview prep needs
     */
    @GetMapping("/suggestions")
    public ResponseEntity<Map<String, Object>> getSuggestions() {
        Map<String, Object> suggestions = Map.of(
            "interview", List.of(
                "How should I answer 'Tell me about yourself'?",
                "What are my key strengths for a senior Java role?",
                "Help me prepare for 'Why should we hire you?'",
                "What's my biggest achievement to highlight?"
            ),
            "analysis", List.of(
                "What are my strongest technical skills?",
                "How many years of Java experience do I have?",
                "What domains have I worked in?",
                "Summarize my leadership experience"
            ),
            "matching", List.of(
                "Rate my fit for a Senior Backend Engineer role",
                "What skills am I missing for cloud-native development?",
                "Compare my experience to a typical Principal Engineer",
                "What certifications would strengthen my profile?"
            )
        );
        
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Resume AI Agent",
            "version", "1.0.0"
        ));
    }

    /**
     * Clear session data
     */
    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<Map<String, String>> clearSession(@PathVariable String sessionId) {
        aiService.clearSession(sessionId);
        return ResponseEntity.ok(Map.of(
            "message", "Session cleared successfully"
        ));
    }
}

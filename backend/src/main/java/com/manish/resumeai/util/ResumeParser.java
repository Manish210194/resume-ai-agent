package com.manish.resumeai.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.Loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
public class ResumeParser {

    /**
     * Extract text from uploaded resume file (PDF or DOCX)
     */
    public String extractText(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }
        
        String lowerFilename = filename.toLowerCase();
        
        if (lowerFilename.endsWith(".pdf")) {
            return extractFromPDF(file.getInputStream());
        } else if (lowerFilename.endsWith(".docx")) {
            return extractFromDOCX(file.getInputStream());
        } else {
            throw new IllegalArgumentException(
                "Unsupported file format. Please upload PDF or DOCX file."
            );
        }
    }

    /**
     * Extract text from PDF file
     */
    private String extractFromPDF(InputStream inputStream) throws IOException {
        try (PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            log.info("Extracted {} characters from PDF", text.length());
            return text;
        }
    }

    /**
     * Extract text from DOCX file
     */
    private String extractFromDOCX(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            StringBuilder text = new StringBuilder();
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            
            for (XWPFParagraph paragraph : paragraphs) {
                text.append(paragraph.getText()).append("\n");
            }
            
            log.info("Extracted {} characters from DOCX", text.length());
            return text.toString();
        }
    }

    /**
     * Validate if file is a supported resume format
     */
    public boolean isValidResumeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null) {
            return false;
        }
        
        String lower = filename.toLowerCase();
        return lower.endsWith(".pdf") || lower.endsWith(".docx");
    }
}

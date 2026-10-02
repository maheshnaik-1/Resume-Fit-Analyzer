package com.resumeanalyzer.service;

import com.resumeanalyzer.exception.InvalidFileException;
import com.resumeanalyzer.exception.PdfParsingException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PdfExtractorService {

    public static final int MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB limit (5,242,880 bytes)

    public String extractText(MultipartFile file) {
        if (file == null) {
            throw new InvalidFileException("The uploaded file is empty. Please upload a valid resume PDF.");
        }

        String filename = file.getOriginalFilename();
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new PdfParsingException("Unable to parse the PDF file. The file may be corrupted, encrypted, or password-protected.", e);
        }

        return extractText(filename, bytes);
    }

    public String extractText(String filename, byte[] bytes) {
        // 1. Validate File Extension (.pdf case-insensitive)
        if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
            throw new InvalidFileException("Invalid file format. Only PDF documents (.pdf) are supported.");
        }

        // 2. Validate Empty File
        if (bytes == null || bytes.length == 0) {
            throw new InvalidFileException("The uploaded file is empty. Please upload a valid resume PDF.");
        }

        // 3. Validate 5 MB Size Limit
        if (bytes.length > MAX_FILE_SIZE) {
            throw new InvalidFileException("File size exceeds the 5 MB limit. Please upload a smaller PDF.");
        }

        // 4. Validate PDF Header Magic Bytes (%PDF)
        if (bytes.length < 4 || bytes[0] != '%' || bytes[1] != 'P' || bytes[2] != 'D' || bytes[3] != 'F') {
            throw new InvalidFileException("Invalid PDF file. The uploaded file is not a valid PDF document.");
        }

        // 5. Extract Text with PDFBox 3.x Loader API
        String text;
        try (PDDocument document = Loader.loadPDF(bytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            text = stripper.getText(document);
        } catch (Exception e) {
            throw new PdfParsingException("Unable to parse the PDF file. The file may be corrupted, encrypted, or password-protected.", e);
        }

        // 6. Validate Extractable Text Content (>= 10 chars)
        String cleanedText = text != null ? text.trim() : "";
        if (cleanedText.length() < 10) {
            throw new InvalidFileException("No readable text found in the PDF. Scanned or image-only documents without an embedded text layer are not supported.");
        }

        return cleanedText;
    }
}

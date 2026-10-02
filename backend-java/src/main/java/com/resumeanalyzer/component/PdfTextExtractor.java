package com.resumeanalyzer.component;

import com.resumeanalyzer.exception.InvalidFileException;
import com.resumeanalyzer.exception.PdfParsingException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class PdfTextExtractor {

    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    public String extractText(MultipartFile file) {
        if (file == null) {
            throw new InvalidFileException("The uploaded file is empty. Please upload a valid resume PDF.");
        }

        String filename = file.getOriginalFilename();
        validateFilename(filename);

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new InvalidFileException("Unable to parse the PDF file. The file may be corrupted, encrypted, or password-protected.");
        }

        return extractText(bytes, filename);
    }

    public String extractText(byte[] pdfBytes, String filename) {
        // 1. Validate File Extension
        validateFilename(filename);

        // 2. Validate Empty File
        if (pdfBytes == null || pdfBytes.length == 0) {
            throw new InvalidFileException("The uploaded file is empty. Please upload a valid resume PDF.");
        }

        // 3. Enforce File Size Limit (5 MB)
        if (pdfBytes.length > MAX_FILE_SIZE) {
            throw new InvalidFileException("File size exceeds the 5 MB limit. Please upload a smaller PDF.");
        }

        // 4. Validate PDF Header Magic Bytes (%PDF)
        if (!hasPdfMagicBytes(pdfBytes)) {
            throw new InvalidFileException("Invalid PDF file. The uploaded file is not a valid PDF document.");
        }

        // 5. Extract Text using PDFBox 3.x in memory
        String text;
        try (PDDocument document = Loader.loadPDF(new RandomAccessReadBuffer(pdfBytes))) {
            if (document.isEncrypted()) {
                throw new PdfParsingException("Unable to parse the PDF file. The file may be corrupted, encrypted, or password-protected.");
            }

            StringBuilder sb = new StringBuilder();
            PDFTextStripper stripper = new PDFTextStripper();
            int pageCount = document.getNumberOfPages();

            for (int i = 1; i <= pageCount; i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String pageText = stripper.getText(document);
                if (pageText != null && !pageText.isEmpty()) {
                    sb.append(pageText).append(" ");
                }
            }
            text = sb.toString();
        } catch (PdfParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new PdfParsingException("Unable to parse the PDF file. The file may be corrupted, encrypted, or password-protected.", e);
        }

        // 6. Validate Extractable Text Content (min 10 chars)
        String cleanedText = text.trim();
        if (cleanedText.isEmpty() || cleanedText.length() < 10) {
            throw new PdfParsingException("No readable text found in the PDF. Scanned or image-only documents without an embedded text layer are not supported.");
        }

        return cleanedText;
    }

    private void validateFilename(String filename) {
        if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
            throw new InvalidFileException("Invalid file format. Only PDF documents (.pdf) are supported.");
        }
    }

    private boolean hasPdfMagicBytes(byte[] bytes) {
        if (bytes == null || bytes.length < 4) {
            return false;
        }
        return bytes[0] == 0x25 && bytes[1] == 0x50 && bytes[2] == 0x44 && bytes[3] == 0x46; // "%PDF"
    }
}

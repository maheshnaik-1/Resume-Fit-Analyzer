package com.resumeanalyzer.service;

import com.resumeanalyzer.exception.InvalidFileException;
import com.resumeanalyzer.exception.PdfParsingException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class PdfExtractorServiceTest {

    private PdfExtractorService extractorService;

    @BeforeEach
    void setUp() {
        extractorService = new PdfExtractorService();
    }

    private byte[] createSamplePdf(String... pageTexts) throws IOException {
        try (PDDocument document = new PDDocument()) {
            for (String text : pageTexts) {
                PDPage page = new PDPage();
                document.addPage(page);
                if (text != null && !text.isEmpty()) {
                    try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                        stream.beginText();
                        stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                        stream.newLineAtOffset(50, 700);
                        stream.showText(text);
                        stream.endText();
                    }
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    @Test
    @DisplayName("1. Valid PDF extracts readable text correctly")
    void testValidPdfExtraction() throws IOException {
        String content = "Experienced Java and Spring Boot Software Engineer with AWS skills.";
        byte[] pdfBytes = createSamplePdf(content);
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", pdfBytes);

        String extracted = extractorService.extractText(file);
        assertNotNull(extracted);
        assertTrue(extracted.contains("Java"));
        assertTrue(extracted.contains("Spring Boot"));
    }

    @Test
    @DisplayName("2. Uppercase .PDF filename is accepted")
    void testUppercasePdfExtension() throws IOException {
        String content = "Experienced Python Developer with FastAPI and SQL expertise.";
        byte[] pdfBytes = createSamplePdf(content);
        MockMultipartFile file = new MockMultipartFile("file", "MY_RESUME.PDF", "application/pdf", pdfBytes);

        String extracted = extractorService.extractText(file);
        assertNotNull(extracted);
        assertTrue(extracted.contains("Python"));
    }

    @Test
    @DisplayName("3. Non-PDF extension throws InvalidFileException")
    void testNonPdfExtension() {
        MockMultipartFile file = new MockMultipartFile("file", "resume.docx", "application/octet-stream", "dummy content".getBytes());
        InvalidFileException ex = assertThrows(InvalidFileException.class, () -> extractorService.extractText(file));
        assertEquals("Invalid file format. Only PDF documents (.pdf) are supported.", ex.getMessage());
    }

    @Test
    @DisplayName("4. Empty file throws InvalidFileException")
    void testEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.pdf", "application/pdf", new byte[0]);
        InvalidFileException ex = assertThrows(InvalidFileException.class, () -> extractorService.extractText(file));
        assertEquals("The uploaded file is empty. Please upload a valid resume PDF.", ex.getMessage());
    }

    @Test
    @DisplayName("5. File size > 5 MB throws InvalidFileException")
    void testFileSizeExceeds5Mb() {
        byte[] largeBytes = new byte[PdfExtractorService.MAX_FILE_SIZE + 1];
        largeBytes[0] = '%';
        largeBytes[1] = 'P';
        largeBytes[2] = 'D';
        largeBytes[3] = 'F';

        MockMultipartFile file = new MockMultipartFile("file", "large.pdf", "application/pdf", largeBytes);
        InvalidFileException ex = assertThrows(InvalidFileException.class, () -> extractorService.extractText(file));
        assertEquals("File size exceeds the 5 MB limit. Please upload a smaller PDF.", ex.getMessage());
    }

    @Test
    @DisplayName("6. Invalid magic bytes throws InvalidFileException")
    void testInvalidMagicBytes() {
        byte[] invalidBytes = "This is not a real PDF document at all.".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "fake.pdf", "application/pdf", invalidBytes);
        InvalidFileException ex = assertThrows(InvalidFileException.class, () -> extractorService.extractText(file));
        assertEquals("Invalid PDF file. The uploaded file is not a valid PDF document.", ex.getMessage());
    }

    @Test
    @DisplayName("7. Corrupted PDF structure throws PdfParsingException")
    void testCorruptedPdf() {
        byte[] corruptedBytes = "%PDF-1.4 corrupted binary payload that fails parsing completely".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "corrupt.pdf", "application/pdf", corruptedBytes);
        PdfParsingException ex = assertThrows(PdfParsingException.class, () -> extractorService.extractText(file));
        assertEquals("Unable to parse the PDF file. The file may be corrupted, encrypted, or password-protected.", ex.getMessage());
    }

    @Test
    @DisplayName("8. PDF with < 10 characters throws InvalidFileException")
    void testShortTextPdf() throws IOException {
        byte[] shortPdf = createSamplePdf("Hi");
        MockMultipartFile file = new MockMultipartFile("file", "short.pdf", "application/pdf", shortPdf);
        InvalidFileException ex = assertThrows(InvalidFileException.class, () -> extractorService.extractText(file));
        assertEquals("No readable text found in the PDF. Scanned or image-only documents without an embedded text layer are not supported.", ex.getMessage());
    }

    @Test
    @DisplayName("9. Multi-page PDF extracts text across all pages")
    void testMultiPagePdfExtraction() throws IOException {
        byte[] multiPagePdf = createSamplePdf(
                "Page 1: Senior Java Architect with Microservices and Docker experience.",
                "Page 2: Education: B.Tech Computer Science. Certifications: AWS."
        );
        MockMultipartFile file = new MockMultipartFile("file", "twopage.pdf", "application/pdf", multiPagePdf);
        String extracted = extractorService.extractText(file);
        assertTrue(extracted.contains("Senior Java Architect"));
        assertTrue(extracted.contains("Education: B.Tech"));
    }
}

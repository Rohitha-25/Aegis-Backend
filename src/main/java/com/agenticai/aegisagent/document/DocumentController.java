package com.agenticai.aegisagent.document;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentUploadService documentUploadService;

    @GetMapping
    public List<Document> getAllDocuments() {
        return documentService.getAllDocuments();
    }

    @PostMapping("/upload")
    public Document uploadDocument(@RequestParam("file") MultipartFile file, @RequestParam("category") String category) throws IOException {
        return documentUploadService.uploadDocument(file, category);
    }
}

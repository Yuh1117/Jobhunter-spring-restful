package vn.vpgh.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.vpgh.jobhunter.domain.response.file.ResUploadFileDTO;
import vn.vpgh.jobhunter.service.FileService;
import vn.vpgh.jobhunter.util.annotation.ApiMessage;
import vn.vpgh.jobhunter.util.error.StorageException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v0.1")
public class FileController {
    @Value("${vpgh.upload-file.base-uri}")
    private String baseURI;

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    @ApiMessage("Upload single file")
    public ResponseEntity<ResUploadFileDTO> uploadSingleFile(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder)
            throws URISyntaxException, IOException, StorageException {

        // Validate file
        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty.");
        }

        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx", "txt");
        boolean isValid = allowedExtensions.stream()
                .anyMatch(item -> file.getOriginalFilename().toLowerCase().endsWith(item));

        if (!isValid) {
            throw new StorageException("Invalid file extension! ONLY ALLOWS " + allowedExtensions.toString());
        }

        // Create a directory if not exist
        this.fileService.createUploadFolder(baseURI + folder);

        // Store file
        String fileName = this.fileService.store(file, folder);
        ResUploadFileDTO dto = new ResUploadFileDTO();
        dto.setFileName(fileName);
        dto.setUploadedAt(Instant.now());

        return ResponseEntity.ok().body(dto);
    }

}

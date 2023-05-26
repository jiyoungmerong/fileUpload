package com.example.practice.service;

import com.example.practice.domain.Photo;
import com.example.practice.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
@Transactional
public class PhotoService {
    private static final String UPLOAD_DIR = "upload/";

    private final PhotoRepository photoRepository;

    public String uploadPhoto(MultipartFile file) throws IOException {
        String photoName = file.getOriginalFilename(); // 업로드된 파일의 원래 파일명 가져오기

        Path targetLocation = getUploadPath().resolve(photoName); // 파일의 최종 경로 생성
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        savePhotoToDatabase(photoName); // 파일명을 DB에 저장

        return "파일 업로드 성공: " + photoName;
    }

    private Path getUploadPath() throws IOException {
        // 업로드 경로 표현(절대경로 + 일관성 있는 경로)
        Path uploadPath = Path.of(UPLOAD_DIR).toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) { // 업로드 경로의 존재 여부 확인
            Files.createDirectories(uploadPath); // 업로드 경로가 존재하지 않을 경우 디렉토리 생성
        }
        return uploadPath;
    }

    private String extractExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return null;
    }

    private void savePhotoToDatabase(String fileName) {
        String extension = extractExtension(fileName); // 파일의 확장자 추출

        String contentType = null;
        if (extension != null) {
            if ("jpg".equalsIgnoreCase(extension) || "jpeg".equalsIgnoreCase(extension)) {
                contentType = "image/jpeg";
            } else if ("png".equalsIgnoreCase(extension)) {
                contentType = "image/png";
            } else if ("gif".equalsIgnoreCase(extension)) {
                contentType = "image/gif";
            } else {
                contentType = "image"; 
            }
        } 

        Photo photo = Photo.builder()
                .photoData(fileName.getBytes())
                .contentType(contentType) // 이미지의 타입 저장
                .build();

        photoRepository.save(photo); // DB에 저장
    }

}



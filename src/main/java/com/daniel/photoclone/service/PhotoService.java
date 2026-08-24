package com.daniel.photoclone.service;

import com.daniel.photoclone.exception.PhotoNotFoundException;
import com.daniel.photoclone.model.Photo;
import com.daniel.photoclone.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoService {

    private final PhotoRepository photoRepository;

    @Transactional
    public Photo uploadPhoto(MultipartFile file, String description, String tags) throws IOException {
        log.info("Uploading: {}", file.getOriginalFilename());
        
        // Simpler validation first
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only images allowed. Got: " + contentType);
        }
        
        Photo photo = Photo.builder()
            .fileName(file.getOriginalFilename())
            .contentType(contentType)
            .description(description != null ? description : "")
            .data(file.getBytes())
            .fileSize(file.getSize())
            .tags(tags)
            .uploadDate(LocalDateTime.now())
            .build();
        
        return photoRepository.save(photo);
    }

    @Transactional(readOnly = true)
    public List<Photo> getAllPhotos() {
        return photoRepository.findAllByOrderByUploadDateDesc();
    }

    @Transactional(readOnly = true)
    public Photo getPhoto(Integer id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new PhotoNotFoundException("Photo not found: " + id));
    }

    @Transactional
    public void deletePhoto(Integer id) {
        if (!photoRepository.existsById(id)) {
            throw new PhotoNotFoundException("Photo not found: " + id);
        }
        photoRepository.deleteById(id);
    }

    @Transactional
    public Photo updatePhoto(Integer id, String description, String tags) {
        Photo photo = getPhoto(id);
        if (description != null) photo.setDescription(description);
        if (tags != null) photo.setTags(tags);
        return photoRepository.save(photo);
    }
    
    @Transactional(readOnly = true)
    public List<Photo> searchPhotos(String keyword) {
        return photoRepository.findByDescriptionContainingIgnoreCase(keyword);
    }
}
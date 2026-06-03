package com.daniel.photoclone.service;

import com.daniel.photoclone.model.Photo;
import com.daniel.photoclone.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * PHOTO SERVICE
 * =============
 * Contains business logic for photo operations
 * 
 * @Service: Marks as Spring service bean
 * @RequiredArgsConstructor: Lombok - creates constructor with final fields
 * @Slf4j: Lombok - creates logger field named 'log'
 * 
 * Service classes typically:
 * 1. Contain business logic
 * 2. Handle transactions
 * 3. Coordinate between repositories
 * 4. Handle exceptions
 */
@Service
@RequiredArgsConstructor
@Slf4j  // Creates: private static final Logger log = LoggerFactory.getLogger(PhotoService.class)
public class PhotoService {

    /**
     * DEPENDENCY INJECTION
     * --------------------
     * Spring automatically injects the PhotoRepository
     * @RequiredArgsConstructor creates constructor:
     * public PhotoService(PhotoRepository photoRepository) {
     *     this.photoRepository = photoRepository;
     * }
     */
    private final PhotoRepository photoRepository;

    /**
     * UPLOAD PHOTO
     * ------------
     * @Transactional: Wraps method in database transaction
     * If method fails, all changes are rolled back
     */
    @Transactional
    public Photo uploadPhoto(MultipartFile file, String description, String tags) throws IOException {
        log.info("Starting photo upload: {}", file.getOriginalFilename());
        
        // Validate file
        validateFile(file);
        
        // Create Photo entity
        Photo photo = new Photo();
        photo.setFileName(file.getOriginalFilename());
        photo.setContentType(file.getContentType());
        photo.setDescription(description);
        photo.setData(file.getBytes());  // Convert file to byte array
        photo.setFileSize(file.getSize());
        photo.setTags(tags);
        
        // Save to database
        Photo savedPhoto = photoRepository.save(photo);
        
        log.info("Photo uploaded successfully: ID={}, Size={} bytes", 
                 savedPhoto.getId(), savedPhoto.getFileSize());
        
        return savedPhoto;
    }
    
    /**
     * FILE VALIDATION
     * ---------------
     * Business logic: Validate file before processing
     */
    private void validateFile(MultipartFile file) {
        log.debug("Validating file: {}", file.getOriginalFilename());
        
        // Check if file is empty
        if (file.isEmpty()) {
            log.error("Upload failed: File is empty");
            throw new IllegalArgumentException("File cannot be empty");
        }
        
        // Check file size (max 10MB)
        long maxSize = 10 * 1024 * 1024; // 10MB in bytes
        if (file.getSize() > maxSize) {
            log.error("Upload failed: File too large. Size: {}, Max: {}", 
                     file.getSize(), maxSize);
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }
        
        // Check file type (only images allowed)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            log.error("Upload failed: Invalid file type. Type: {}", contentType);
            throw new IllegalArgumentException("Only image files are allowed");
        }
        
        log.debug("File validation passed");
    }

    /**
     * GET ALL PHOTOS
     * --------------
     * Read-only transaction (optimization)
     */
    @Transactional(readOnly = true)
    public List<Photo> getAllPhotos() {
        log.info("Fetching all photos");
        return photoRepository.findAllByOrderByUploadDateDesc();
    }

    /**
     * GET PHOTO BY ID
     * ---------------
     * Throws custom exception if not found
     */
    @Transactional(readOnly = true)
    public Photo getPhoto(Integer id) {
        log.debug("Fetching photo with ID: {}", id);
        
        return photoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Photo not found with ID: {}", id);
                    return new PhotoNotFoundException("Photo not found with id: " + id);
                });
    }

    /**
     * SEARCH PHOTOS
     * -------------
     */
    @Transactional(readOnly = true)
    public List<Photo> searchPhotos(String keyword) {
        log.info("Searching photos with keyword: {}", keyword);
        return photoRepository.findByDescriptionContainingIgnoreCase(keyword);
    }

    /**
     * DELETE PHOTO
     * ------------
     */
    @Transactional
    public void deletePhoto(Integer id) {
        log.info("Deleting photo with ID: {}", id);
        
        // Check if photo exists
        if (!photoRepository.existsById(id)) {
            throw new PhotoNotFoundException("Photo not found with id: " + id);
        }
        
        photoRepository.deleteById(id);
        log.info("Photo deleted successfully: ID={}", id);
    }

    /**
     * UPDATE PHOTO DESCRIPTION
     * ------------------------
     */
    @Transactional
    public Photo updatePhotoDescription(Integer id, String description, String tags) {
        log.info("Updating photo: ID={}", id);
        
        // Get existing photo
        Photo photo = getPhoto(id);
        
        // Update fields
        photo.setDescription(description);
        photo.setTags(tags);
        
        // Save updates (merge)
        Photo updatedPhoto = photoRepository.save(photo);
        
        log.info("Photo updated: ID={}", updatedPhoto.getId());
        return updatedPhoto;
    }

    /**
     * CUSTOM EXCEPTION
     * ----------------
     * Inner class for photo-specific exceptions
     */
    public static class PhotoNotFoundException extends RuntimeException {
        public PhotoNotFoundException(String message) {
            super(message);
        }
    }
}
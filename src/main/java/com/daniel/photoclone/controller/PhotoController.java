package com.daniel.photoclone.controller;

import com.daniel.photoclone.model.Photo;
import com.daniel.photoclone.exception.*;
import com.daniel.photoclone.service.PhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
@Tag(name = "Photo Management", description = "Upload, view, update, and delete photos")
@CrossOrigin
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a photo", description = "Upload an image file with description and tags")
    public ResponseEntity<?> uploadPhoto(
            @Parameter(description = "Image file (JPEG, PNG,JPG)") 
            @RequestParam("file") MultipartFile file,
            
            @Parameter(description = "Photo description")
            @RequestParam("description") String description,
            
            @Parameter(description = "Comma-separated tags")
            @RequestParam(value = "tags", required = false) String tags) {
        
        try {
            Photo photo = photoService.uploadPhoto(file, description, tags);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", photo.getId());
            response.put("message", "Photo uploaded successfully");
            response.put("fileName", photo.getFileName());
            response.put("fileSize", photo.getFileSize());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Upload failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    @Operation(summary = "Get all photos", description = "Returns list of all photos")
    public ResponseEntity<List<Photo>> getAllPhotos() {
        return ResponseEntity.ok(photoService.getAllPhotos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get photo by ID", description = "Returns photo metadata")
    public ResponseEntity<?> getPhoto(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(photoService.getPhoto(id));
        } catch (PhotoNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/{id}/image", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    @Operation(summary = "Get photo image", description = "Returns the actual image bytes")
    public ResponseEntity<?> getPhotoImage(@PathVariable Integer id) {
        try {
            Photo photo = photoService.getPhoto(id);
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType(photo.getContentType()))
                    .body(photo.getData());
        } catch (PhotoNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update photo", description = "Update description and tags")
    public ResponseEntity<?> updatePhoto(
            @PathVariable Integer id,
            @RequestParam String description,
            @RequestParam(required = false) String tags) {
        try {
            Photo updated = photoService.updatePhoto(id, description, tags);
            return ResponseEntity.ok(updated);
        } catch (PhotoNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete photo", description = "Delete a photo by ID")
    public ResponseEntity<?> deletePhoto(@PathVariable Integer id) {
        try {
            photoService.deletePhoto(id);
            return ResponseEntity.noContent().build();
        } catch (PhotoNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search photos", description = "Search by description keyword")
    public ResponseEntity<List<Photo>> searchPhotos(@RequestParam String q) {
        return ResponseEntity.ok(photoService.searchPhotos(q));
    }
}
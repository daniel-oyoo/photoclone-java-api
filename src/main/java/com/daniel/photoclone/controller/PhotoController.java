package com.daniel.photoclone.controller;

import com.daniel.photoclone.model.Photo;
import com.daniel.photoclone.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PHOTO CONTROLLER
 * ================
 * Handles HTTP requests for photo operations
 * 
 * @RestController: Combines @Controller and @ResponseBody
 *                 - @Controller: Marks as Spring MVC controller
 *                 - @ResponseBody: Return values are written to response body (JSON)
 * 
 * @RequestMapping: Base URL for all methods in this controller
 * 
 * REST API Design Principles:
 * 1. Use HTTP methods appropriately (GET, POST, PUT, DELETE)
 * 2. Return proper HTTP status codes
 * 3. Use consistent URL patterns
 * 4. Return JSON responses
 */
@RestController
@RequestMapping("/api/photos")  // All URLs start with /api/photos
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    /**
     * POST /api/photos/upload
     * -----------------------
     * Upload a new photo
     * 
     * @PostMapping: Maps HTTP POST requests
     * 
     * @RequestParam: Binds form parameters
     * - "file": The uploaded file (MultipartFile)
     * - "description": Photo description
     * - "tags": Optional tags
     * 
     * ResponseEntity: Allows full control over HTTP response
     * - Body: The created photo
     * - Status: 201 CREATED
     * - Headers: Can set custom headers
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("description") String description,
            @RequestParam(value = "tags", required = false) String tags) {
        
        System.out.println("=== Photo Upload Request ===");
        System.out.println("File: " + file.getOriginalFilename());
        System.out.println("Size: " + file.getSize() + " bytes");
        System.out.println("Type: " + file.getContentType());
        System.out.println("Description: " + description);
        System.out.println("Tags: " + tags);
        
        try {
            // Call service to upload photo
            Photo uploadedPhoto = photoService.uploadPhoto(file, description, tags);
            
            // Return 201 CREATED with photo in response body
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(uploadedPhoto);
                    
        } catch (IOException e) {
            // Handle file IO errors
            System.err.println("File upload error: " + e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to upload file: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
                    
        } catch (IllegalArgumentException e) {
            // Handle validation errors
            System.err.println("Validation error: " + e.getMessage());
            
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity
                    .badRequest()  // HTTP 400
                    .body(error);
        }
    }

    /**
     * GET /api/photos
     * ---------------
     * Get all photos
     * 
     * @GetMapping: Maps HTTP GET requests
     * Returns: List of photos as JSON
     */
    @GetMapping
    public ResponseEntity<List<Photo>> getAllPhotos() {
        System.out.println("Fetching all photos");
        
        List<Photo> photos = photoService.getAllPhotos();
        
        // Return 200 OK with photos list
        return ResponseEntity.ok(photos);
    }

    /**
     * GET /api/photos/{id}
     * --------------------
     * Get photo by ID
     * 
     * @PathVariable: Extracts value from URL path
     * {id} in URL maps to Integer id parameter
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPhoto(@PathVariable Integer id) {
        System.out.println("Fetching photo with ID: " + id);
        
        try {
            Photo photo = photoService.getPhoto(id);
            return ResponseEntity.ok(photo);
            
        } catch (PhotoService.PhotoNotFoundException e) {
            // Photo not found
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)  // HTTP 404
                    .body(error);
        }
    }

    /**
     * GET /api/photos/{id}/image
     * ---------------------------
     * Get the actual image file
     * 
     * Returns: Image bytes with proper content type header
     */
    @SuppressWarnings("null")
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getPhotoImage(@PathVariable Integer id) {
        System.out.println("Fetching image data for photo ID: " + id);
        
        try {
            Photo photo = photoService.getPhoto(id);
            
            // Create HTTP headers
            HttpHeaders headers = new HttpHeaders();
            
            // Set content type from photo (e.g., "image/jpeg")
            headers.setContentType(MediaType.parseMediaType(photo.getContentType()));
            
            // Set content length
            headers.setContentLength(photo.getFileSize());
            
            // Suggest filename for download
            headers.set("Content-Disposition", 
                       "inline; filename=\"" + photo.getFileName() + "\"");
            
            // Return image bytes with headers
            return new ResponseEntity<>(photo.getData(), headers, HttpStatus.OK);
            
        } catch (PhotoService.PhotoNotFoundException e) {
            // Return 404 if photo not found
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/photos/search
     * ----------------------
     * Search photos by keyword
     * 
     * @RequestParam: Extracts query parameters
     * Example: /api/photos/search?keyword=beach
     */
    @GetMapping("/search")
    public ResponseEntity<List<Photo>> searchPhotos(@RequestParam String keyword) {
        System.out.println("Searching photos with keyword: " + keyword);
        
        List<Photo> photos = photoService.searchPhotos(keyword);
        return ResponseEntity.ok(photos);
    }

    /**
     * PUT /api/photos/{id}
     * --------------------
     * Update photo description
     * 
     * PUT vs POST:
     * - POST: Create new resource
     * - PUT: Update existing resource
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePhoto(
            @PathVariable Integer id,
            @RequestParam String description,
            @RequestParam(value = "tags", required = false) String tags) {
        
        System.out.println("Updating photo ID: " + id);
        System.out.println("New description: " + description);
        System.out.println("New tags: " + tags);
        
        try {
            Photo updatedPhoto = photoService.updatePhotoDescription(id, description, tags);
            return ResponseEntity.ok(updatedPhoto);
            
        } catch (PhotoService.PhotoNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * DELETE /api/photos/{id}
     * -----------------------
     * Delete a photo
     * 
     * Returns: 204 NO CONTENT on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePhoto(@PathVariable Integer id) {
        System.out.println("Deleting photo ID: " + id);
        
        try {
            photoService.deletePhoto(id);
            
            // 204 NO CONTENT - Successful delete, no body
            return ResponseEntity.noContent().build();
            
        } catch (PhotoService.PhotoNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
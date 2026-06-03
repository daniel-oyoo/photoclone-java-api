package com.daniel.photoclone.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * PHOTO ENTITY CLASS
 * ==================
 * Maps to 'photos' table in database
 * 
 * @Entity: Marks this class as a JPA entity
 * @Table: Specifies the table name (optional)
 * @Data: Lombok annotation - generates getters, setters, toString, equals, hashCode
 */
@Entity
@Table(name = "photos")  // Maps to 'photos' table
@Data  // Lombok: Auto-generates getters, setters, toString, etc.
public class Photo {

    /**
     * PRIMARY KEY
     * -----------
     * @Id: Marks this field as primary key
     * @GeneratedValue: Auto-generates ID values
     * Strategy.IDENTITY: Database generates the ID (like AUTO_INCREMENT in MySQL)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * FILE NAME COLUMN
     * ----------------
     * @Column: Maps to database column
     * nullable=false: Column cannot be null
     */
    @Column(nullable = false)
    private String fileName;

    /**
     * CONTENT TYPE
     * ------------
     * Stores MIME type (e.g., "image/jpeg", "image/png")
     */
    @Column(nullable = false)
    private String contentType;

    /**
     * DESCRIPTION
     * -----------
     * User-provided description of the photo
     */
    @Column(nullable = false)
    private String description;

    /**
     * PHOTO DATA (BINARY)
     * -------------------
     * @Lob: Large Object annotation
     * Used for storing large data (images, documents)
     * Maps to BLOB (Binary Large Object) in database
     */
    @Lob
    @Column(nullable = false)
    private byte[] data;

    /**
     * UPLOAD DATE
     * -----------
     * Automatically set when photo is uploaded
     */
    @Column(nullable = false)
    private LocalDateTime uploadDate;

    /**
     * FILE SIZE
     * ---------
     * Size in bytes
     */
    @Column(nullable = false)
    private Long fileSize;

    /**
     * TAGS
     * ----
     * Comma-separated tags (e.g., "vacation,beach,sunset")
     * Could be normalized to separate table in real app
     */
    @Column
    private String tags;

    /**
     * LIFECYCLE CALLBACK METHOD
     * -------------------------
     * @PrePersist: Executes BEFORE entity is saved to database
     * Sets uploadDate automatically
     */
    @PrePersist
    protected void onCreate() {
        uploadDate = LocalDateTime.now();
        System.out.println("Setting upload date for photo: " + fileName);
    }
    
    /**
     * OPTIONAL: Constructor without Lombok
     * Uncomment if you're not using Lombok
     */
    /*
    public Photo() {
        // Default constructor required by JPA
    }
    
    // Getters and setters would go here without Lombok
    */
}























/*
take photo 
upload to server update ,delete,crud

design(google photos)
1
resources
images,users,people,photos,movies , screen recording
2
url
/images--list all photos/
/images/add-- add images
/image/{image_url/id}---list a single photo/search/edit/update/delete a single image a single image
/image/view/{image_url/id}
/image/update/{image_url/id}
/image/remove{image_url/id}
/collections---group photos by category eg whatsapp,download,camera
/images/collections
3
actions
add
delete
edit/update
view
4
data types
view send empty request return image
add send image receive confirmation
delete/edit/update send iamge id receive confrimation
formats -----image formats jpeg,png,
              image_id formats integer,url
*/
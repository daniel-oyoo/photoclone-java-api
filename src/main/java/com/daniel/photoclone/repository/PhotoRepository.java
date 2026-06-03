package com.daniel.photoclone.repository;

import com.daniel.photoclone.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PHOTO REPOSITORY INTERFACE
 * ===========================
 * 
 * @Repository: Spring annotation - marks as repository bean
 * 
 * JpaRepository<Photo, Integer> provides:
 * - save(), saveAll()
 * - findById(), findAll()
 * - delete(), deleteAll()
 * - count(), existsById()
 * - And many more...
 * 
 * Spring Data JPA automatically implements this interface at runtime!
 * No need to write implementation class.
 */
@Repository
public interface PhotoRepository extends JpaRepository<Photo, Integer> {
    // Integer is the type of the primary key (Photo.id)
    
    /**
     * CUSTOM QUERY METHOD 1: Find by description containing text
     * ----------------------------------------------------------
     * Spring Data JPA creates query automatically from method name!
     * Naming convention: findBy[Property][Condition]
     * 
     * This will generate: 
     * SELECT * FROM photos WHERE LOWER(description) LIKE LOWER('%keyword%')
     */
    List<Photo> findByDescriptionContainingIgnoreCase(String keyword);
    
    /**
     * CUSTOM QUERY METHOD 2: Find by tag using @Query annotation
     * ---------------------------------------------------------
     * Write custom JPQL (Java Persistence Query Language)
     * JPQL uses entity names and properties, not table/column names
     */
    @Query("SELECT p FROM Photo p WHERE p.tags LIKE %:tag%")
    List<Photo> findByTag(@Param("tag") String tag);
    
    /**
     * CUSTOM QUERY METHOD 3: Find all sorted by upload date (newest first)
     * --------------------------------------------------------------------
     * Method name automatically creates ORDER BY clause
     */
    List<Photo> findAllByOrderByUploadDateDesc();
    
    /**
     * CUSTOM QUERY METHOD 4: Find photos larger than given size
     * ---------------------------------------------------------
     * Using JPQL with comparison operator
     */
    @Query("SELECT p FROM Photo p WHERE p.fileSize > :minSize")
    List<Photo> findLargePhotos(@Param("minSize") Long minSize);
    
    /**
     * CUSTOM QUERY METHOD 5: Count photos by content type
     * ---------------------------------------------------
     * Using native SQL query (use only when JPQL can't do it)
     */
    @Query(value = "SELECT content_type, COUNT(*) FROM photos GROUP BY content_type", 
           nativeQuery = true)
    List<Object[]> countByContentType();
}
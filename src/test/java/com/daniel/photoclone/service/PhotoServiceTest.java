package com.daniel.photoclone.service;

import com.daniel.photoclone.model.Photo;
import com.daniel.photoclone.repository.PhotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {

    @Mock
    private PhotoRepository photoRepository;

    @InjectMocks
    private PhotoService photoService;

    @Test
    void uploadPhotoStoresImageMetadataAndBytes() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.png", "image/png", new byte[] {1, 2, 3});
        when(photoRepository.save(any(Photo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Photo saved = photoService.uploadPhoto(file, "A test photo", "travel,summer");

        assertThat(saved.getFileName()).isEqualTo("photo.png");
        assertThat(saved.getContentType()).isEqualTo("image/png");
        assertThat(saved.getData()).containsExactly(1, 2, 3);
        assertThat(saved.getFileSize()).isEqualTo(3);
    }

    @Test
    void uploadPhotoRejectsNonImages() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "notes.txt", "text/plain", "not an image".getBytes());

        assertThatThrownBy(() -> photoService.uploadPhoto(file, "Notes", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Only images allowed");
    }
}
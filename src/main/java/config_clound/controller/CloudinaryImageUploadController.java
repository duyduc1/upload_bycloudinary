package config_clound.controller;

import com.cloudinary.Cloudinary;
import config_clound.enities.ImageEntity;
import config_clound.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cloudinary/upload")
@RequiredArgsConstructor
public class CloudinaryImageUploadController {

    private final CloudinaryService cloudinaryService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            Map<String, Object> data = this.cloudinaryService.upload(file);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImageEntity> getImage(@PathVariable Long id) {
        try {
            ImageEntity imageEntity = cloudinaryService.getImageById(id);
            return new ResponseEntity<>(imageEntity, HttpStatus.OK);
        }catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public List<ImageEntity> getAllImages() {
        return cloudinaryService.getAllImages();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteImage(@PathVariable Long id) {
        try {
            cloudinaryService.deleteImageById(id);
            return new ResponseEntity<>(Map.of("message", "Image deleted"), HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateImage(@PathVariable Long id, @RequestParam("image") MultipartFile newFile) {
        try {
            cloudinaryService.updateImage(id, newFile);
            return new ResponseEntity<>(Map.of("message", "Image updated successfully"), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(Map.of("error", "Failed to update image in Cloudinary: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}

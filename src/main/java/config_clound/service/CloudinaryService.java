package config_clound.service;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import config_clound.enities.ImageEntity;
import config_clound.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private final ImageRepository imageRepository;

    public Map<String, Object> upload(MultipartFile file) {
        try {
            Map<String, Object> data = this.cloudinary.uploader().upload(file.getBytes(), Map.of());

            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setUrl((String) data.get("url"));
            imageEntity.setPublicId((String) data.get("public_id"));

            imageRepository.save(imageEntity);

            return data;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    public ImageEntity getImageById(Long id) {
        return this.imageRepository.findById(id).orElse(null);
    }

    public List<ImageEntity> getAllImages() {
        return this.imageRepository.findAll();
    }

    public void deleteImageById(Long id) throws IOException {

        ImageEntity imageEntity = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        cloudinary.uploader().destroy(imageEntity.getPublicId(), ObjectUtils.emptyMap());

        imageRepository.delete(imageEntity);
    }

    public void updateImage(Long id, MultipartFile newFile) throws IOException {
        // Find the existing image entity in the database
        ImageEntity imageEntity = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        // Delete the old image from Cloudinary
        cloudinary.uploader().destroy(imageEntity.getPublicId(), ObjectUtils.emptyMap());

        // Upload the new image to Cloudinary
        Map<String, Object> newData = this.cloudinary.uploader().upload(newFile.getBytes(), Map.of());

        // Update the image entity with new data
        imageEntity.setUrl((String) newData.get("url"));
        imageEntity.setPublicId((String) newData.get("public_id"));

        // Save the updated entity back to the database
        imageRepository.save(imageEntity);
    }
}

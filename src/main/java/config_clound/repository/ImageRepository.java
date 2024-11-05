package config_clound.repository;

import config_clound.enities.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ImageEntity , Long> {
}

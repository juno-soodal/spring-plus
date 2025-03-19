package org.example.expert.domain.common.image.repository;

import org.example.expert.domain.common.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}

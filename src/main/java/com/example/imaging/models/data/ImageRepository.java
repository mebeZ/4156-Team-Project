package com.example.imaging.models.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.imaging.models.Image;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface ImageRepository extends JpaRepository<Image, String> {
    Optional<Image> findByName(String imageName);
}

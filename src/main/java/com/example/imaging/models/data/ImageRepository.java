package com.example.imaging.models.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.imaging.models.Image;

@Repository
public interface ImageRepository extends CrudRepository<Image, Long> {
}

package com.example.imaging.models.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.imaging.models.Client;

@Repository
public interface ClientRepository extends CrudRepository<Client, String> {
}

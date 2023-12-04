package com.example.imaging.controllers;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.imaging.models.Client;
import com.example.imaging.models.data.ClientRepository;

@RestController
public class ClientController {

    @Autowired
    private ClientRepository clientDao;

    @PostMapping("/add-client")
    public Client createClient(@RequestParam(value="accessToken") String token) {
        return clientDao.save(new Client(token));
    }

    @GetMapping("/client-list")
    public Iterable<Client> getAllClients() {
        return clientDao.findAll();
    }
}

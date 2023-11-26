package com.example.imaging.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.imaging.models.Client;
import com.example.imaging.models.data.ClientRepository;

@RestController
public class ClientController {

    @Autowired
    private ClientRepository clientDao;

    @GetMapping("/add-client")
    public Client createClient(@RequestParam(value="accessToken") String token) {
        // Create new client object 
        Client newClient = new Client(token);
        // Save it to the client db
        newClient = clientDao.save(newClient);
        return newClient;
    }

    @GetMapping("/client-list")
    public Iterable<Client> getClients() {
        return clientDao.findAll();
    }
}

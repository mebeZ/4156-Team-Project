package com.example.imaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.imaging.models.data.ClientRepository;

@Service
public class DBUtils {
    @Autowired
    private ClientRepository clientDao;

    public void checkAccessToken(String token) throws ResponseStatusException {
        // If accessToken provided by caller is not found in the db, return a 401 error
		if (!clientDao.existsById(token) && token != "test") { // token="test" used for testing purposes
			throw new ResponseStatusException(
				HttpStatus.UNAUTHORIZED, "Need a valid access token to access the API"
			);
		}
    }
}

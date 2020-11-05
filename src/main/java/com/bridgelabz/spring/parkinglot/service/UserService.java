package com.bridgelabz.spring.parkinglot.service;

import com.bridgelabz.spring.parkinglot.dto.UserDTO;
import com.bridgelabz.spring.parkinglot.model.UserDetails;
import com.bridgelabz.spring.parkinglot.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    IUserRepository userRepository;

    public String registerUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmailId()).isPresent())
            return "Email already exists";
        UserDetails user = new UserDetails();
        user.setEmail(userDTO.getEmailId());
        user.setPassword(userDTO.getPassword());
        userRepository.save(user);
        return "Registration done";

    }
}

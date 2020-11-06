package com.bridgelabz.spring.parkinglot.service;

import com.bridgelabz.spring.parkinglot.dto.UserDTO;
import com.bridgelabz.spring.parkinglot.exception.UserException;
import com.bridgelabz.spring.parkinglot.model.UserDetails;
import com.bridgelabz.spring.parkinglot.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    IUserRepository userRepository;

    @Autowired
    JavaMailSender javaMailSender;


    public void sendSimpleMessage(UserDTO userDTO) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("tejasvinirpk@gmail.com");
        message.setTo(userDTO.getEmailId());
        message.setSubject("Registration message");
        message.setText("Your registration is done successfully");
        javaMailSender.send(message);
    }


    public String registerUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmailId()).isPresent())
            return "Email already exists";
        UserDetails user = new UserDetails();
        user.setEmail(userDTO.getEmailId());
        user.setPassword(userDTO.getPassword());
        userRepository.save(user);
        sendSimpleMessage(userDTO);
        return "Registration done";
    }

    public UserDetails findByEmail(String emailId) {
        return userRepository.findByEmail(emailId).orElseThrow(
                () -> new UserException("Record not found")) ;
    }

    public String loginUser(UserDTO userDTO) {
        UserDetails user = findByEmail(userDTO.getEmailId());
        if (user.getPassword().equals(userDTO.getPassword()))
            return "Right password";
        return "Wrong password";
    }

    public String deleteUser(UserDTO userDTO){
        userRepository.delete(findByEmail(userDTO.getEmailId()));
        return "User deleted";
    }
}

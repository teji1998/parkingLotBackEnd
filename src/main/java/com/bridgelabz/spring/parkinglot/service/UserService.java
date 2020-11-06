package com.bridgelabz.spring.parkinglot.service;

import com.bridgelabz.spring.parkinglot.dto.Password;
import com.bridgelabz.spring.parkinglot.dto.UserDTO;
import com.bridgelabz.spring.parkinglot.exception.UserException;
import com.bridgelabz.spring.parkinglot.model.UserDetails;
import com.bridgelabz.spring.parkinglot.repository.IUserRepository;
import com.bridgelabz.spring.parkinglot.utility.TokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    IUserRepository userRepository;

    @Autowired
    JavaMailSender javaMailSender;

    public void sendSimpleMessage(UserDTO userDTO, String message) throws MailException {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("tejasvinirpk@gmail.com");
        mail.setTo(userDTO.getEmailId());
        mail.setSubject("Registration message");
        mail.setText(getVerificationUrl(message));
        javaMailSender.send(mail);
    }

    public void sendTokenUrl(UserDTO userDTO, String message) throws MailException {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("tejasvinirpk@gmail.com");
        mail.setTo(userDTO.getEmailId());
        mail.setSubject("Token link");
        mail.setText(message);
        javaMailSender.send(mail);
    }

    public String getVerificationUrl(String token) {
        return "http://localhost:8080/user/"+token;
    }


    public String registerUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmailId()).isPresent())
            return "Email already exists";
        UserDetails user = new UserDetails();
        user.setName(userDTO.getName());
        user.setMobileNo(userDTO.getMobileNo());
        user.setEmail(userDTO.getEmailId());
        user.setPassword(userDTO.getPassword());
        UserDetails registeredUser = userRepository.save(user);
        String token = TokenUtility.getToken(registeredUser.getId());
        sendSimpleMessage(userDTO,token);
        return "Registration done";
    }

    public UserDetails findByEmail(String emailId) {
        return userRepository.findByEmail(emailId).orElseThrow(
                () -> new UserException("Record not found")) ;
    }

    public String loginUser(UserDTO userDTO) {
        UserDetails user = findByEmail(userDTO.getEmailId());
        if (user.getEmail().equals(userDTO.getEmailId())) {
            if (user.isVerified()) {
                if (user.getPassword().equals(userDTO.getPassword())) {
                    Optional<UserDetails> userDetails = userRepository.findByEmail(userDTO.getEmailId());
                    if (userDetails.isPresent()) {
                        String token = TokenUtility.getToken(userDetails.get().getId());
                        sendTokenUrl(userDTO, token);
                        return "Please check your email";
                    }
                    return "Successfully logged in";
                }
                return "Wrong password given";
            }
            return "Please do the verification first";
        }
        return "Email with which you have logged in is wrong";
    }

    public String deleteUser(UserDTO userDTO){
        userRepository.delete(findByEmail(userDTO.getEmailId()));
        return "User deleted";
    }

    public String verifyingUser(String token) {
        Optional<UserDetails> user = userRepository.findById((TokenUtility.decodeJWT(token)));
        user.get().setVerified(true);
        userRepository.save(user.get());
        return "Verification is successful";
    }


    public String generatingPasswordToken(UserDTO userDTO) {
        Optional<UserDetails> user = userRepository.findByEmail(userDTO.getEmailId());
        if (user.isPresent()) {
            String token = TokenUtility.getToken(user.get().getId());
            sendTokenUrl(userDTO,token);
            return "Check your email please!";
        }
        return "Email given is incorrect";
    }

    public String settingPassword(String token, Password password) {
        Optional<UserDetails> user = userRepository.findById(TokenUtility.decodeJWT(token));
        if (password.getNewPassword().equals(password.getConfirmPassword())) {
            user.get().setPassword(password.getConfirmPassword());
            userRepository.save(user.get());
            return "Password has been changed";
        }
        return "Password is incorrect!";
    }
}

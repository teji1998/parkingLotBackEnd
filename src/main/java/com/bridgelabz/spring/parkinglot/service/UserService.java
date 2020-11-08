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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    IUserRepository userRepository;

    @Autowired
    JavaMailSender javaMailSender;
/*
    @Autowired
    PasswordEncoder passwordEncoder;*/

    public void sendSimpleMessage(UserDTO userDTO, String message) throws MailException {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("tejasvinirpk@gmail.com");
        mail.setTo(userDTO.getEmailId());
        mail.setSubject("Registration message");
        mail.setText(getVerificationUrl(message));
        javaMailSender.send(mail);
    }

    public void sendForgotPasswordMessage(UserDTO userDTO ,String mail) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("tejasvinirpk@gmail.com");
        message.setTo(userDTO.getEmailId());
        message.setSubject("Forgot password message");
        message.setText("You will need to reset your password with token :" +mail );
        javaMailSender.send(message);
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
       /* user.setPassword(passwordEncoder.encode(userDTO.getPassword()));*/
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
                if (user.getPassword().equals(userDTO.getPassword()))
                   return TokenUtility.getToken(user.getId());
                else
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

    public String forgotPassword(UserDTO userDTO) {
        Optional<UserDetails> user = userRepository.findByEmail(userDTO.getEmailId());
        if (user.isPresent()) {
            String token = TokenUtility.getToken(user.get().getId());
            sendForgotPasswordMessage(userDTO,token);
            return "Please check the mail that you have provided ";
        }
        return "Email provided by you is incorrect";
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

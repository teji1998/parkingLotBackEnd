package com.bridgelabz.spring.parkinglot.controller;

import com.bridgelabz.spring.parkinglot.dto.UserDTO;
import com.bridgelabz.spring.parkinglot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping()
    public ResponseEntity registerUser(@RequestBody UserDTO userDTO) {
        String registrationMessage = userService.registerUser(userDTO);
        return new ResponseEntity(registrationMessage, HttpStatus.OK);
    }
    @GetMapping("/loginuser")
    public ResponseEntity getLoginUser(@RequestBody UserDTO userDTO) {
        String user = userService.loginUser(userDTO);
        return new ResponseEntity(user, HttpStatus.OK);
    }
}
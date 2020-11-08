package com.bridgelabz.spring.parkinglot.controller;

import com.bridgelabz.spring.parkinglot.dto.Password;
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

    @PostMapping("/loginuser")
    public ResponseEntity getLoginUser(@RequestBody UserDTO userDTO) {
        String user = userService.loginUser(userDTO);
        return new ResponseEntity(user, HttpStatus.OK);
    }

    @PostMapping("/forgotpassword")
    public ResponseEntity getForgotPassword(@RequestBody UserDTO userDTO) {
        String user = userService.forgotPassword(userDTO);
        return new ResponseEntity(user, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity deleteMessage(@RequestBody UserDTO userDTO) {
        String deleteMessage = userService.deleteUser(userDTO);
        return new ResponseEntity(deleteMessage, HttpStatus.OK);
    }

    @GetMapping("/{token}")
    public ResponseEntity verifyUser(@PathVariable String token) {
        String response = userService.verifyingUser(token);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping("/token")
    public ResponseEntity forgotPassword(@RequestHeader String token, @RequestBody Password password) {
        String message = userService.settingPassword(token,password);
        return new ResponseEntity(message,HttpStatus.OK);
    }

}

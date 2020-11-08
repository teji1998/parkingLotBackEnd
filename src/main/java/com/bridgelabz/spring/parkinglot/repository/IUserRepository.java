package com.bridgelabz.spring.parkinglot.repository;

import com.bridgelabz.spring.parkinglot.model.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<UserDetails, Long> {

    Optional<UserDetails> findByEmail(String email);

}


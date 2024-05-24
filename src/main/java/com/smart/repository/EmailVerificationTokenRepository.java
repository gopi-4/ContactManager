package com.smart.repository;

import com.smart.entities.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Integer> {
    EmailVerificationToken findByToken(String token);
}

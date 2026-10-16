package com.voixdesagesse.VoixDeSagesse.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.voixdesagesse.VoixDeSagesse.entity.OTP;

@Repository
public interface OTPRepository extends MongoRepository<OTP, String> {
    List<OTP> findByCreationTimeBefore(LocalDateTime expiry);
}

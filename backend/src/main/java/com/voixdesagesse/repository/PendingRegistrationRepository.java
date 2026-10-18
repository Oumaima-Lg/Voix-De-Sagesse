package com.voixdesagesse.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.voixdesagesse.entity.PendingRegistration;

@Repository
public interface PendingRegistrationRepository extends MongoRepository<PendingRegistration, String> {
    List<PendingRegistration> findByCreationTimeBefore(LocalDateTime expiry);
}

package com.voixdesagesse.VoixDeSagesse.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.voixdesagesse.VoixDeSagesse.entity.User;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {

    public Optional<User> findByEmail(String email);
}

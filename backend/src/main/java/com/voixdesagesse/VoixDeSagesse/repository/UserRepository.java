package com.voixdesagesse.VoixDeSagesse.repository;

import java.util.Optional;

import com.voixdesagesse.VoixDeSagesse.entity.User;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {
    // Tu peux ajouter des méthodes personnalisées si nécessaire

    // Exemple: Trouver un utilisateur par son email
    public Optional<User> findByEmail(String email);
}

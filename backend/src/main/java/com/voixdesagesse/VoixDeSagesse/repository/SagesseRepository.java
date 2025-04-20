package com.voixdesagesse.VoixDeSagesse.repository;

import com.voixdesagesse.VoixDeSagesse.entity.Sagesse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SagesseRepository extends MongoRepository<Sagesse, String> {
    // Tu peux ajouter des méthodes personnalisées si nécessaire

    // Exemple : Trouver une sagesse par type
    List<Sagesse> findByTypeSagesse(String typeSagesse);
}

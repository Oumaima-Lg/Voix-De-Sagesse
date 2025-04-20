package com.voixdesagesse.VoixDeSagesse.repository;

import com.voixdesagesse.VoixDeSagesse.entity.Histoire;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistoireRepository extends MongoRepository<Histoire, String> {
    // Tu peux ajouter des méthodes personnalisées si nécessaire

    // Exemple : Trouver une histoire par type d'histoire
    List<Histoire> findByTypeHistoire(String typeHistoire);
}

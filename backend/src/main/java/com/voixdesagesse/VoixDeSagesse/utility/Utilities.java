package com.voixdesagesse.VoixDeSagesse.utility;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.voixdesagesse.VoixDeSagesse.entity.Sequence;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;


@Component
public class Utilities {

    private static MongoOperations mongoOperation;

    @Autowired
    public void setMongoOperation(MongoOperations mongoOperation) {
        Utilities.mongoOperation = mongoOperation;
    }

    public static Long getNextSequence(String key) throws ArticlaException {
        Query query = new Query(Criteria.where("_id").is(key));
        Update update = new Update();
        update.inc("seq", 1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true).upsert(true);
        Sequence seq = mongoOperation.findAndModify(query, update, options, Sequence.class);
        if (seq == null)
            throw new ArticlaException("Unable to get sequence id for key : " + key);
        return seq.getSeq();
    }

    public static String generateOTP() {
        StringBuilder otp = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();

    }

    public static String getElapsedTime(LocalDateTime datePublication) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(datePublication, now);
        Period period = Period.between(datePublication.toLocalDate(), now.toLocalDate());

        if (period.getYears() > 0) {
            return period.getYears() + "an" + (period.getYears() > 1 ? "s" : "");
        } else if (period.getMonths() > 0) {
            return period.getMonths() + "mois";
        } else if (period.getDays() > 0) {
            return period.getDays() + "j";
        } else if (duration.toHours() > 0) {
            return duration.toHours() + "h";
        } else if (duration.toMinutes() > 0) {
            return duration.toMinutes() + "min";
        } else {
            return "à l'instant";
        }
    }
}

// 🔹 Query (classe)
// Sert à construire une requête MongoDB.
// Utilisée pour définir des critères de recherche dans une collection Mongo.
// Query query = new Query(Criteria.where("_id").is("user_1"));
// 👉 Cela correspond à la requête Mongo suivante :
// { "_id": "user_1" }

// 🔹 Criteria (classe)
// Permet de définir les conditions (filtres) de ta requête.
// Elle est utilisée à l’intérieur d’un objet Query.
// Criteria.where("age").gte(18) // age >= 18
// Criteria.where("status").is("active")

// 🔹 Update (classe)
// Sert à définir les modifications à appliquer sur un document existant.
// Update update = new Update().inc("seq", 1);
// 👉 Cela signifie : incrémenter le champ seq de 1 (équivalent à $inc en
// MongoDB).

// 🔹 FindAndModifyOptions (classe)
// returnNew(true) : retourne le nouveau document modifié (sinon il retourne
// l'ancien).
// upsert(true) : crée un document si aucun ne correspond à la requête (comme
// insertIfNotExists).
// FindAndModifyOptions options = new
// FindAndModifyOptions().returnNew(true).upsert(true);

// 🔹 findAndModify (méthode)
// Définie dans : MongoOperations ou MongoTemplate
// Cette méthode permet de :
// Trouver un document
// Le modifier
// Et retourner l’ancien ou le nouveau document
// <T> T findAndModify(Query query, Update update, FindAndModifyOptions options,
// Class<T> entityClass);

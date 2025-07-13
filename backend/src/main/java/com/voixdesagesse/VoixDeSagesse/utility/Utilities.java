package com.voixdesagesse.VoixDeSagesse.utility;

import java.security.SecureRandom;

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
        // La requête query est créée pour chercher un document où la clé _id est égale à key
        Update update = new Update();
        // Permet de spécifier les opérations d'update à effectuer sur le document MongoDB.
        update.inc("seq", 1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true).upsert(true);
        Sequence seq = mongoOperation.findAndModify(query, update, options, Sequence.class);
        if (seq == null) throw new ArticlaException("Unable to get sequence id for key : " + key);
        return seq.getSeq();
    }

    public static String generateOTP () {
        StringBuilder otp  = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
        
    }
}

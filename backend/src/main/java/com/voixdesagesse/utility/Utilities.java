package com.voixdesagesse.utility;

import java.security.SecureRandom;
import java.util.Base64;
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

import com.voixdesagesse.entity.Sequence;
import com.voixdesagesse.exception.VoixDeSagesseException;


@Component
public class Utilities {

    private static MongoOperations mongoOperation;

    @Autowired
    public void setMongoOperation(MongoOperations mongoOperation) {
        Utilities.mongoOperation = mongoOperation;
    }

    public static Long getNextSequence(String key) throws VoixDeSagesseException {
        Query query = new Query(Criteria.where("_id").is(key));
        Update update = new Update();
        update.inc("seq", 1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true).upsert(true);
        Sequence seq = mongoOperation.findAndModify(query, update, options, Sequence.class);
        if (seq == null)
            throw new VoixDeSagesseException("Unable to get sequence id for key : " + key);
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

    // The start of a text, cut between two words, with an ellipsis when it had to be shortened.
    public static String excerpt(String text, int maxLength) {
        String value = text == null ? "" : text.strip();
        if (value.length() <= maxLength)
            return value;
        int cut = value.lastIndexOf(' ', maxLength);
        if (cut < maxLength / 2)
            cut = maxLength;
        return value.substring(0, cut).strip().replaceAll("[,;:.]$", "") + "…";
    }

    public static String generateResetToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
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

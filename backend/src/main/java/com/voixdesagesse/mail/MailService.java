package com.voixdesagesse.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.voixdesagesse.exception.VoixDeSagesseException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String mailFrom;

    // Any sending failure is reported as MAIL_FAILED; the details stay in the logs.
    public void sendHtml(String to, String subject, String html) throws VoixDeSagesseException {
        try {
            MimeMessage mm = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mm, true);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(html, true);
            message.setFrom(mailFrom);
            mailSender.send(mm);
        } catch (MessagingException | MailException e) {
            log.error("Could not send email", e);
            throw new VoixDeSagesseException("MAIL_FAILED");
        }
    }
}

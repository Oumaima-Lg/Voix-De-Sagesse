package com.voixdesagesse.VoixDeSagesse.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.voixdesagesse.VoixDeSagesse.dto.LoginDTO;
import com.voixdesagesse.VoixDeSagesse.dto.ResponseDTO;
import com.voixdesagesse.VoixDeSagesse.dto.UserDTO;
import com.voixdesagesse.VoixDeSagesse.entity.Data;
import com.voixdesagesse.VoixDeSagesse.entity.OTP;
import com.voixdesagesse.VoixDeSagesse.entity.User;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;
import com.voixdesagesse.VoixDeSagesse.repository.OTPRepository;
import com.voixdesagesse.VoixDeSagesse.repository.UserRepository;
import com.voixdesagesse.VoixDeSagesse.utility.Utilities;

import jakarta.mail.internet.MimeMessage;

@Service(value = "userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public UserDTO registerUser(UserDTO userDTO) throws ArticlaException {

        Optional<User> optional = userRepository.findByEmail(userDTO.getEmail());
        if (optional.isPresent())
            throw new ArticlaException("USER_FOUND");

        userDTO.setId(Utilities.getNextSequence("users"));
        userDTO.setMotdepasse(passwordEncoder.encode(userDTO.getMotdepasse()));

        User user = userDTO.toEntity();
        user = userRepository.save(user);

        return user.toDTO();
    }

    @Override
    public UserDTO loginUser(LoginDTO loginDTO) throws ArticlaException {

        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new ArticlaException("USER_NOT_FOUND"));

        if (!passwordEncoder.matches(loginDTO.getMotdepasse(), user.getMotdepasse()))
            throw new ArticlaException("INVALID_CREDENTIALS");

        return user.toDTO();
    }

    @Override
    public Boolean sendOtp(String email) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ArticlaException("USER_NOT_FOUND"));
        MimeMessage mm = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mm, true);
        message.setTo(email);
        message.setSubject("Your OTP Code");
        String genOtp = Utilities.generateOTP();
        OTP otp = new OTP(email, genOtp, LocalDateTime.now());
        otpRepository.save(otp);
        // message.setText("Your Code is : " + genOtp, false);
        message.setText(Data.getMessageBody(genOtp, user.getNom(), user.getPrenom()), true);
        message.setFrom("laghjibioumaima2003@gmail.com");
        mailSender.send(mm);
        return true;
    }

    @Override
    public Boolean verifyOtp(String email, String otp) throws ArticlaException {
        OTP otpEntity = otpRepository.findById(email).orElseThrow(() -> new ArticlaException("OTP_NOT_FOUND"));

        if (!otpEntity.getOtpCode().equals((otp)))
            throw new ArticlaException("OTP_INCORRECT");
        return true;

    }

    @Override
    public ResponseDTO changePassword(LoginDTO loginDTO) throws ArticlaException {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new ArticlaException("USER_NOT_FOUND"));
        user.setMotdepasse(passwordEncoder.encode(loginDTO.getMotdepasse()));
        userRepository.save(user);
        return new ResponseDTO("Password changed successfully.");
    }

    @Scheduled(fixedRate = 60000)
    public void removeExpiredOTPs() {
        LocalDateTime expiry = LocalDateTime.now().minusMinutes(5);
        List<OTP> expiredOTPs = otpRepository.findByCreationTimeBefore(expiry);
        if (!expiredOTPs.isEmpty()) {
            otpRepository.deleteAll(expiredOTPs);
            System.out.println("Removed " + expiredOTPs.size() + " expired OTPs.");
        }
    }

}

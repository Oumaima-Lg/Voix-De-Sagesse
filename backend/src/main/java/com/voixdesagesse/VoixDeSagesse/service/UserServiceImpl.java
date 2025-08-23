package com.voixdesagesse.VoixDeSagesse.service;

import java.time.LocalDateTime;
import java.util.HashSet;
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
import com.voixdesagesse.VoixDeSagesse.dto.UserProfileDTO;
import com.voixdesagesse.VoixDeSagesse.dto.UserRegistrationDTO;
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
    public UserRegistrationDTO registerUser(UserRegistrationDTO userDTO) throws ArticlaException {

        Optional<User> optional = userRepository.findByEmail(userDTO.getEmail());
        if (optional.isPresent())
            throw new ArticlaException("USER_FOUND");

        userDTO.setId(Utilities.getNextSequence("users"));
        userDTO.setMotdepasse(passwordEncoder.encode(userDTO.getMotdepasse()));

        User user = userDTO.toEntity();
        user.setUsername(user.getPrenom());
        user.setFollowersCount(0L);
        user.setContentCount(0L);
        user.setFollowingCount(0L);
        user.setLikedArticlesId(new HashSet<>());
        user.setFollowingId(new HashSet<>());
        user = userRepository.save(user);

        return user.toRegisterDTO();
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
        }
    }

    @Override
    public UserProfileDTO updateUserProfile(UserProfileDTO profileDTO) throws ArticlaException {
        User user = userRepository.findById(profileDTO.getId()).orElseThrow(() -> new ArticlaException("USER_NOT_FOUND"));
        user.setNom(profileDTO.getNom());
        user.setPrenom(profileDTO.getPrenom());
        // save into a repo Image
        user.setProfilePicture(profileDTO.getProfilePicture());
        user.setBio(profileDTO.getBio());
        userRepository.save(user);
        return profileDTO;
    }

    @Override
    public User getUserById(long userId) throws ArticlaException {
        return userRepository.findById(userId).orElseThrow(() -> new ArticlaException("USER_NOT_FOUND"));
    }

}

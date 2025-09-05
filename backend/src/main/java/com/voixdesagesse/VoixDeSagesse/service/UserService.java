package com.voixdesagesse.VoixDeSagesse.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.voixdesagesse.VoixDeSagesse.dto.LoginDTO;
import com.voixdesagesse.VoixDeSagesse.dto.ResponseDTO;
import com.voixdesagesse.VoixDeSagesse.dto.UserDTO;
import com.voixdesagesse.VoixDeSagesse.dto.UserProfileDTO;
import com.voixdesagesse.VoixDeSagesse.dto.UserRegistrationDTO;
import com.voixdesagesse.VoixDeSagesse.entity.User;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;


// @Service
public interface UserService {

    public UserRegistrationDTO  registerUser(UserRegistrationDTO userDTO) throws ArticlaException;

    public UserDTO getUserByEmail(String email) throws ArticlaException;

    public UserDTO loginUser(LoginDTO loginDTO) throws ArticlaException;

    public Boolean sendOtp(String email) throws Exception;

    public Boolean verifyOtp(String email, String otp) throws ArticlaException;

    public ResponseDTO changePassword(LoginDTO loginDTO) throws ArticlaException;

    public UserProfileDTO updateUserProfile(UserProfileDTO profileDTO) throws ArticlaException;

    public User getUserById(long userId) throws ArticlaException;

    public void addLikedArticle(Long currentUserId, Long articleId);

    public void removeLikedArticle(Long currentUserId, Long articleId);

    public void incrementLikesReceived(Long userId);

    public void decrementLikesReceived(Long userId);

    public UserProfileDTO getUserProfileById(long userId) throws ArticlaException;
    
    public String saveProfilePicture(MultipartFile file, Long userId) throws IOException;

    public void incrementContentCount(Long userId);
    
    public void decrementContentCount(Long userId);

    // Nouvelles méthodes pour le système de suivi
    public void followUser(Long currentUserId, Long targetUserId);

    public void unfollowUser(Long currentUserId, Long targetUserId);

    public boolean isFollowing(Long currentUserId, Long targetUserId);
    
    // Méthodes internes (appelées par followUser/unfollowUser)
    public void addFollowing(Long currentUserId, Long targetUserId);

    public void removeFollowing(Long currentUserId, Long targetUserId);
    
    public void incrementFollowingCount(Long userId);
    
    public void decrementFollowingCount(Long userId);
    
    public void incrementFollowersCount(Long userId);
    
    public void decrementFollowersCount(Long userId);

    public  List<User> findAllFollowingUsersById(Set<Long> followingIds);

    public List<User> findByFollowingIdContaining(Long userId);


}
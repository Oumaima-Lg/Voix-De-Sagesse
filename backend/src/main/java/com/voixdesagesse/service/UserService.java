package com.voixdesagesse.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.voixdesagesse.dto.AccountType;
import com.voixdesagesse.dto.ResetPasswordRequest;
import com.voixdesagesse.dto.ResponseDTO;
import com.voixdesagesse.dto.UserDTO;
import com.voixdesagesse.dto.UserProfileDTO;
import com.voixdesagesse.dto.UserRegistrationDTO;
import com.voixdesagesse.entity.User;
import com.voixdesagesse.exception.VoixDeSagesseException;



public interface UserService {

    public User getCurrentUser() throws VoixDeSagesseException;

    public ResponseDTO startRegistration(UserRegistrationDTO userDTO) throws VoixDeSagesseException;

    public UserRegistrationDTO confirmRegistration(String email, String otp) throws VoixDeSagesseException;

    public User createAccount(String nom, String prenom, String email, String password, AccountType accountType)
            throws VoixDeSagesseException;

    public UserDTO getUserByEmail(String email) throws VoixDeSagesseException;

    public Boolean sendOtp(String email) throws VoixDeSagesseException;

    public String verifyOtp(String email, String otp) throws VoixDeSagesseException;

    public ResponseDTO changePassword(ResetPasswordRequest request) throws VoixDeSagesseException;

    public UserProfileDTO updateUserProfile(Long userId, UserProfileDTO profileDTO) throws VoixDeSagesseException;

    public User getUserById(long userId) throws VoixDeSagesseException;

    public boolean addLikedArticle(Long currentUserId, Long articleId);

    public boolean removeLikedArticle(Long currentUserId, Long articleId);

    public void incrementLikesReceived(Long userId);

    public void decrementLikesReceived(Long userId);

    
    public String saveProfilePicture(MultipartFile file, Long userId) throws IOException;

    public void incrementContentCount(Long userId);
    
    public void decrementContentCount(Long userId);

    public void followUser(Long currentUserId, Long targetUserId) throws VoixDeSagesseException;

    public void unfollowUser(Long currentUserId, Long targetUserId) throws VoixDeSagesseException;

    public boolean isFollowing(Long currentUserId, Long targetUserId);

    public void addFollowing(Long currentUserId, Long targetUserId);

    public void removeFollowing(Long currentUserId, Long targetUserId);
    
    public void incrementFollowingCount(Long userId);
    
    public void decrementFollowingCount(Long userId);
    
    public void incrementFollowersCount(Long userId);
    
    public void decrementFollowersCount(Long userId);

    public  List<User> findAllFollowingUsersById(Set<Long> followingIds);

    public List<User> findByFollowingIdContaining(Long userId);

    public void saveArticle(Long userId, Long articleId) throws VoixDeSagesseException;

    public void unsaveArticle(Long userId, Long articleId) throws VoixDeSagesseException;

    public boolean isArticleSaved(Long userId, Long articleId);

    public void removeArticleFromAllUsersLikes(Long articleId);
    
    public void removeArticleFromAllUsersSaved(Long articleId);
    
    public void decrementLikesReceivedByAmount(Long userId, Long amount);

    public void addReportedArticle(Long userId, Long articleId) throws VoixDeSagesseException;

    public void removeReportedArticle(Long userId, Long articleId) throws VoixDeSagesseException;

    public long getTotalUsers();

    public long getNewUsersToday();

    public List<UserDTO> getAllUsers() throws VoixDeSagesseException;

    public void deleteUserAccount(Long userId) throws VoixDeSagesseException;

}



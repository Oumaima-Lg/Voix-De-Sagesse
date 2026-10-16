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



public interface UserService {

    public User getCurrentUser() throws ArticlaException;

    public UserRegistrationDTO  registerUser(UserRegistrationDTO userDTO) throws ArticlaException;

    public UserDTO getUserByEmail(String email) throws ArticlaException;

    public UserDTO loginUser(LoginDTO loginDTO) throws ArticlaException;

    public Boolean sendOtp(String email) throws Exception;

    public Boolean verifyOtp(String email, String otp) throws ArticlaException;

    public ResponseDTO changePassword(LoginDTO loginDTO) throws ArticlaException;

    public UserProfileDTO updateUserProfile(UserProfileDTO profileDTO) throws ArticlaException;

    public User getUserById(long userId) throws ArticlaException;

    public void addLikedArticle(Long currentUserId, Long articleId) throws ArticlaException;

    public void removeLikedArticle(Long currentUserId, Long articleId) throws ArticlaException;

    public void incrementLikesReceived(Long userId) throws ArticlaException;

    public void decrementLikesReceived(Long userId) throws ArticlaException;

    public UserProfileDTO getUserProfileById(long userId) throws ArticlaException;

    // public Resource getProfilePicture(String filename);
    
    public String saveProfilePicture(MultipartFile file, Long userId) throws IOException;

    public void incrementContentCount(Long userId);
    
    public void decrementContentCount(Long userId);

    public void followUser(Long currentUserId, Long targetUserId) throws ArticlaException;

    public void unfollowUser(Long currentUserId, Long targetUserId) throws ArticlaException;

    public boolean isFollowing(Long currentUserId, Long targetUserId);

    public void addFollowing(Long currentUserId, Long targetUserId);

    public void removeFollowing(Long currentUserId, Long targetUserId);
    
    public void incrementFollowingCount(Long userId);
    
    public void decrementFollowingCount(Long userId);
    
    public void incrementFollowersCount(Long userId);
    
    public void decrementFollowersCount(Long userId);

    public  List<User> findAllFollowingUsersById(Set<Long> followingIds);

    public List<User> findByFollowingIdContaining(Long userId);

    public void saveArticle(Long userId, Long articleId) throws ArticlaException;

    public void unsaveArticle(Long userId, Long articleId) throws ArticlaException;

    public boolean isArticleSaved(Long userId, Long articleId);

    public void removeArticleFromAllUsersLikes(Long articleId);
    
    public void removeArticleFromAllUsersSaved(Long articleId);
    
    public void decrementLikesReceivedByAmount(Long userId, Long amount);

    public void addReportedArticle(Long userId, Long articleId) throws ArticlaException;

    public void removeReportedArticle(Long userId, Long articleId) throws ArticlaException;

    public long getTotalUsers();

    public long getNewUsersToday();

    public List<UserDTO> getAllUsers() throws ArticlaException;

    public void deleteUserAccount(Long userId) throws ArticlaException;

}



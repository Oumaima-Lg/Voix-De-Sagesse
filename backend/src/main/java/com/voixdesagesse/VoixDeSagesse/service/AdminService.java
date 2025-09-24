package com.voixdesagesse.VoixDeSagesse.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.voixdesagesse.VoixDeSagesse.dto.UserDTO;
import com.voixdesagesse.VoixDeSagesse.entity.User;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;
import com.voixdesagesse.VoixDeSagesse.repository.ArticleRepository;
import com.voixdesagesse.VoixDeSagesse.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    public List<UserDTO> getAllUsers() throws ArticlaException {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(User::toDTO)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getDashboardStats() {
        log.info("Récupération des statistiques du tableau de bord");

        long totalUsers = userRepository.count();
        long totalArticles = articleRepository.count();

        long newUsersToday = getUsersCreatedToday();
        long newArticlesToday = getArticlesCreatedToday();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalArticles", totalArticles);
        stats.put("newUsersToday", newUsersToday);
        stats.put("newArticlesToday", newArticlesToday);

        return stats;
    }

    private long getUsersCreatedToday() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        return userRepository.countByCreationDateAfter(startOfDay);
    }

    private long getArticlesCreatedToday() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        return articleRepository.countByDatePublicationAfter(startOfDay);
    }
}
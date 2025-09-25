package com.voixdesagesse.VoixDeSagesse.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voixdesagesse.VoixDeSagesse.dto.SignalDTO;
import com.voixdesagesse.VoixDeSagesse.dto.SignalStatus;
import com.voixdesagesse.VoixDeSagesse.entity.Article;
import com.voixdesagesse.VoixDeSagesse.entity.Signal;
import com.voixdesagesse.VoixDeSagesse.entity.User;
import com.voixdesagesse.VoixDeSagesse.exception.ArticlaException;
import com.voixdesagesse.VoixDeSagesse.repository.ArticleRepository;
import com.voixdesagesse.VoixDeSagesse.repository.SignalRepository;
import com.voixdesagesse.VoixDeSagesse.utility.Utilities;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SignalService {

    private final SignalRepository signalRepository;
    private final UserService userService;
    private final ArticleRepository articleRepository;

    @Transactional
    public SignalDTO reportArticle(Long reporterId, Long articleId, String reason, String description) throws ArticlaException {
        // Vérifier si l'utilisateur et l'article existent
        User reporter = userService.getUserById(reporterId);
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new ArticlaException("Article non trouvé"));
        
        User reportedUser = userService.getUserById(article.getUserId());

        // Vérifier si l'utilisateur n'a pas déjà signalé cet article
        if (signalRepository.existsByReporterIdAndArticleId(reporterId, articleId)) {
            throw new ArticlaException("Vous avez déjà signalé cet article");
        }

        // Créer le signal
        Signal signal = new Signal();
        signal.setId(Utilities.getNextSequence("signals"));
        signal.setReporterId(reporterId);
        signal.setReportedUserId(article.getUserId());
        signal.setArticleId(articleId);
        signal.setReason(reason);
        signal.setDescription(description);
        signal.setStatus(SignalStatus.PENDING);
        signal.setCreatedAt(LocalDateTime.now());

        Signal savedSignal = signalRepository.save(signal);

        // Ajouter l'article à la liste des articles signalés de l'utilisateur
        userService.addReportedArticle(reporterId, articleId);

        log.info("Article signalé - Reporter: {}, Article: {}, Raison: {}", reporterId, articleId, reason);

        return convertToDTO(savedSignal, reporter, reportedUser, article);
    }

    public List<SignalDTO> getPendingSignals() throws ArticlaException {
        List<Signal> signals = signalRepository.findByStatusOrderByCreatedAtDesc(SignalStatus.PENDING);
        return signals.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SignalDTO> getSignalsByReporter(Long reporterId) throws ArticlaException {
        List<Signal> signals = signalRepository.findByReporterIdOrderByCreatedAtDesc(reporterId);
        return signals.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SignalDTO processSignal(Long signalId, SignalStatus status, String adminComment, Long adminId) throws ArticlaException {
        Signal signal = signalRepository.findById(signalId)
            .orElseThrow(() -> new ArticlaException("Signal non trouvé"));

        signal.setStatus(status);
        signal.setAdminComment(adminComment);
        signal.setAdminId(adminId);
        signal.setProcessedAt(LocalDateTime.now());

        Signal updatedSignal = signalRepository.save(signal);

        log.info("Signal traité - ID: {}, Statut: {}, Admin: {}", signalId, status, adminId);

        return convertToDTO(updatedSignal);
    }

    private SignalDTO convertToDTO(Signal signal) {
        try {
            User reporter = userService.getUserById(signal.getReporterId());
            User reportedUser = userService.getUserById(signal.getReportedUserId());
            Article article = articleRepository.findById(signal.getArticleId()).orElse(null);

            return convertToDTO(signal, reporter, reportedUser, article);
        } catch (ArticlaException e) {
            log.error("Erreur lors de la conversion du signal: " + e.getMessage());
            return null;
        }
    }

    private SignalDTO convertToDTO(Signal signal, User reporter, User reportedUser, Article article) {
        SignalDTO dto = new SignalDTO();
        dto.setId(signal.getId());
        dto.setReporterId(signal.getReporterId());
        dto.setReporterName(reporter.getNom() + " " + reporter.getPrenom());
        dto.setReportedUserId(signal.getReportedUserId());
        dto.setReportedUserName(reportedUser.getNom() + " " + reportedUser.getPrenom());
        dto.setArticleId(signal.getArticleId());
        dto.setArticleTitle(article != null ? article.getTitle() : "Article supprimé");
        dto.setReason(signal.getReason());
        dto.setDescription(signal.getDescription());
        dto.setStatus(signal.getStatus());
        dto.setCreatedAt(signal.getCreatedAt());
        dto.setProcessedAt(signal.getProcessedAt());
        dto.setAdminId(signal.getAdminId());
        dto.setAdminComment(signal.getAdminComment());

        return dto;
    }
}
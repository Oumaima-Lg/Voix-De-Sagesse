package com.voixdesagesse.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.voixdesagesse.dto.SignalDTO;
import com.voixdesagesse.dto.SignalStatus;
import com.voixdesagesse.entity.Article;
import com.voixdesagesse.mail.EmailTemplates;
import com.voixdesagesse.mail.MailService;
import com.voixdesagesse.entity.Signal;
import com.voixdesagesse.entity.User;
import com.voixdesagesse.exception.VoixDeSagesseException;
import com.voixdesagesse.repository.ArticleRepository;
import com.voixdesagesse.repository.SignalRepository;
import com.voixdesagesse.utility.Utilities;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service(value = "signalService")
@Slf4j
@RequiredArgsConstructor
public class SignalServiceImpl implements SignalService {

    private final SignalRepository signalRepository;
    private final UserService userService;
    private final ArticleService articleService;
    private final ArticleRepository articleRepository;
    private final MailService mailService;
    private final NotificationService notificationService;

    @Override
    public SignalDTO reportArticle(Long reporterId, Long articleId, String reason, String description)
            throws VoixDeSagesseException {
        User reporter = userService.getUserById(reporterId);
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new VoixDeSagesseException("ARTICLE_NOT_FOUND"));

        User reportedUser = userService.getUserById(article.getUserId());

        if (signalRepository.existsByReporterIdAndArticleId(reporterId, articleId)) {
            throw new VoixDeSagesseException("ALREADY_REPORTED");
        }

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

        userService.addReportedArticle(reporterId, articleId);

        log.info("Article signalé - Reporter: {}, Article: {}, Raison: {}", reporterId, articleId, reason);

        return convertToDTO(savedSignal, reporter, reportedUser, article);
    }

    @Override
    public List<SignalDTO> getPendingSignals() throws VoixDeSagesseException {
        List<Signal> signals = signalRepository.findByStatusOrderByCreatedAtDesc(SignalStatus.PENDING);
        return signals.stream()
                .map(this::convertToDTO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public SignalDTO processSignal(Long signalId, SignalStatus status, String adminComment, Long adminId)
            throws VoixDeSagesseException {
        if (status != SignalStatus.APPROVED && status != SignalStatus.REJECTED)
            throw new VoixDeSagesseException("SIGNAL_STATUS_INVALID");
        Signal signal = signalRepository.findById(signalId)
                .orElseThrow(() -> new VoixDeSagesseException("SIGNAL_NOT_FOUND"));
        if (signal.getStatus() != SignalStatus.PENDING)
            throw new VoixDeSagesseException("SIGNAL_ALREADY_PROCESSED");

        LocalDateTime now = LocalDateTime.now();
        signal.setStatus(status);
        signal.setAdminComment(adminComment);
        signal.setAdminId(adminId);
        signal.setProcessedAt(now);
        Signal savedSignal = signalRepository.save(signal);

        if (status == SignalStatus.APPROVED) {
            signalRepository.settlePendingByArticleId(signal.getArticleId(), SignalStatus.APPROVED, now, adminId);
            Optional<Article> article = articleRepository.findById(signal.getArticleId());
            if (article.isPresent()) {
                articleService.deleteArticle(article.get().getId(), article.get().getUserId());
                log.info("Article supprimé - ID: {}", signal.getArticleId());
                notificationService.articleRemoved(article.get());
                notifyAuthor(article.get());
            }
        }

        log.info("Signal traité - ID: {}, Statut: {}, Admin: {}", signalId, status, adminId);
        return convertToDTO(savedSignal);
    }

    private void notifyAuthor(Article article) {
        try {
            User author = userService.getUserById(article.getUserId());
            mailService.sendHtml(author.getEmail(), "[Avertissement] Votre article a été supprimé suite à des signalements",
                    EmailTemplates.articleDeleted(author.getNom(), author.getPrenom(), article.label()));
        } catch (VoixDeSagesseException e) {
            log.warn("Author of deleted article {} not notified: {}", article.getId(), e.getMessage());
        }
    }

    private SignalDTO convertToDTO(Signal signal) {
        try {
            User reporter = userService.getUserById(signal.getReporterId());
            User reportedUser = userService.getUserById(signal.getReportedUserId());
            Article article = articleRepository.findById(signal.getArticleId()).orElse(null);

            return convertToDTO(signal, reporter, reportedUser, article);
        } catch (VoixDeSagesseException e) {
            log.error("Erreur lors de la conversion du signal: " + e.getMessage());
            return null;
        }
    }

    private SignalDTO convertToDTO(Signal signal, User reporter, User reportedUser, Article article) {
        SignalDTO dto = new SignalDTO();
        dto.setId(signal.getId());
        dto.setReporterId(signal.getReporterId());
        dto.setReporterName(reporter.getNom() + " " + reporter.getPrenom());
        dto.setReporterEmail(reporter.getEmail());
        dto.setReportedUserId(signal.getReportedUserId());
        dto.setReportedUserName(reportedUser.getNom() + " " + reportedUser.getPrenom());
        dto.setReportedUserEmail(reportedUser.getEmail());
        dto.setArticleId(signal.getArticleId());

        if (article != null) {
            dto.setArticleTitle(article.getTitle());
            dto.setArticleContent(article.getContent());
            dto.setArticleSource(article.getSource());
            dto.setArticleLesson(article.getLesson());
            dto.setArticleTags(article.getTags() != null ? String.join(", ", article.getTags()) : "");
            dto.setArticleType(article.getType() != null ? article.getType().toString() : "");
            dto.setArticleCategory(article.getCategory() != null ? article.getCategory().toString() : "");
        } else {
            dto.setArticleTitle("Article supprimé");
            dto.setArticleContent("");
            dto.setArticleSource("");
            dto.setArticleLesson("");
            dto.setArticleTags("");
            dto.setArticleType("");
            dto.setArticleCategory("");
        }

        dto.setReason(signal.getReason());
        dto.setDescription(signal.getDescription());
        dto.setStatus(signal.getStatus());
        dto.setCreatedAt(signal.getCreatedAt());
        dto.setProcessedAt(signal.getProcessedAt());
        dto.setAdminId(signal.getAdminId());
        dto.setAdminComment(signal.getAdminComment());

        return dto;
    }

    @Override
    public long getTotalSignals() {
        return signalRepository.count();
    }

    @Override
    public long getPendingSignalsCount() {
        return signalRepository.countByStatus(SignalStatus.PENDING);
    }

    @Override
    public List<SignalDTO> getAllSignals() throws VoixDeSagesseException {
        List<Signal> signals = signalRepository.findAllByOrderByCreatedAtDesc();
        return signals.stream()
                .map(this::convertToDTO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByArticleId(Long id) {
        signalRepository.deleteByArticleId(id);
    }

    @Override
    public void deleteByReporterId(Long userId) {
        signalRepository.deleteByReporterId(userId);
    }

    @Override
    public void deleteByReportedUserId(Long userId) {
        signalRepository.deleteByReportedUserId(userId);
    }
}
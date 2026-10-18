package com.voixdesagesse.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import com.voixdesagesse.dto.SignalStatus;
import com.voixdesagesse.entity.Signal;

@Repository
public interface SignalRepository extends MongoRepository<Signal, Long> {

    List<Signal> findByStatus(SignalStatus status);

    List<Signal> findByReporterId(Long reporterId);

    List<Signal> findByArticleId(Long articleId);

    Optional<Signal> findByReporterIdAndArticleId(Long reporterId, Long articleId);

    List<Signal> findByReportedUserId(Long reportedUserId);

    boolean existsByReporterIdAndArticleId(Long reporterId, Long articleId);

    List<Signal> findByStatusOrderByCreatedAtDesc(SignalStatus status);

    List<Signal> findByReporterIdOrderByCreatedAtDesc(Long reporterId);

    long countByStatus(SignalStatus status);

    List<Signal> findAllByOrderByCreatedAtDesc();

    void deleteByArticleId(Long articleId);

    // Settles at once every report still waiting on an article (when the article is deleted).
    @Query("{ 'articleId': ?0, 'status': 'PENDING' }")
    @Update("{ '$set': { 'status': ?1, 'processedAt': ?2, 'adminId': ?3 } }")
    long settlePendingByArticleId(Long articleId, SignalStatus status, LocalDateTime processedAt, Long adminId);

    void deleteByReporterId(Long reporterId);

    void deleteByReportedUserId(Long reportedUserId);

}
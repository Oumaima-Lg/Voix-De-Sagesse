package com.voixdesagesse.VoixDeSagesse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.voixdesagesse.VoixDeSagesse.dto.SignalStatus;
import com.voixdesagesse.VoixDeSagesse.entity.Signal;

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

}
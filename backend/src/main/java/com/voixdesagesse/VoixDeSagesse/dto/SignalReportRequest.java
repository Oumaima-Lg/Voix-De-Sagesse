package com.voixdesagesse.VoixDeSagesse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SignalReportRequest(
    @JsonProperty("reporterId") Long reporterId,
    @JsonProperty("articleId") Long articleId,
    @JsonProperty("reason") String reason,
    @JsonProperty("description") String description
) {}
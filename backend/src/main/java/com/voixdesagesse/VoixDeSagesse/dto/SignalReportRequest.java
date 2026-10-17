package com.voixdesagesse.VoixDeSagesse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SignalReportRequest(
    @JsonProperty("articleId") Long articleId,
    @JsonProperty("reason") String reason,
    @JsonProperty("description") String description
) {}
package com.voixdesagesse.dto;

public enum SignalStatus {
    PENDING,
    // The admin deleted the article.
    APPROVED,
    // The admin kept the article.
    REJECTED,
    // The author deleted the article before it was reviewed.
    CLOSED
}
package com.voixdesagesse.dto;

public enum NotificationType {
    LIKE,
    COMMENT,
    FOLLOW,
    // Someone added the article to their favorites.
    SAVE,
    // The admin deleted the article after reports.
    ARTICLE_REMOVED
}

package com.ashish.clubs.common.config;

public final class AppConstants {

    // Common API prefixes
    public static final String API_V1_PREFIX = "/api/v1";

    // Pagination defaults
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";

    // Kafka Topics (Centralized definitions to avoid typos)
    public static final String KAFKA_TOPIC_USER_EVENTS = "user-events";
    public static final String KAFKA_TOPIC_CLUB_EVENTS = "club-events";
    public static final String KAFKA_TOPIC_FEED_EVENTS = "feed-events";
    public static final String KAFKA_TOPIC_NOTIFICATION_REQUESTS = "notification-requests";
    public static final String KAFKA_TOPIC_ANALYTICS_EVENTS = "analytics-events";

    // Redis Cache Names
    public static final String CACHE_USERS = "usersCache";
    public static final String CACHE_CLUBS = "clubsCache";
    public static final String CACHE_POSTS = "postsCache";

    private AppConstants() {
        // restrict instantiation
    }
}
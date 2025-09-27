package com.cyberfoot.domain.model;

public record MatchEvent(
    int seq,
    int minute,
    String type,
    int goalsHome,
    int goalsAway,
    String message
) {}

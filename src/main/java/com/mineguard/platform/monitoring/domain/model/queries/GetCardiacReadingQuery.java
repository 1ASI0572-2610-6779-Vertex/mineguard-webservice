package com.mineguard.platform.monitoring.domain.model.queries;

/** The latest cardiac reading for a single driving session — never a flat, unfiltered collection. */
public record GetCardiacReadingQuery(Long sessionId) {
}

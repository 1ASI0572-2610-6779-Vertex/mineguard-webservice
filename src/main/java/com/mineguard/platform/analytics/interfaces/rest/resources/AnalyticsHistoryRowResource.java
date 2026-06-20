package com.mineguard.platform.analytics.interfaces.rest.resources;
public record AnalyticsHistoryRowResource(Long id, String date, String time, String criticality,
                                          String criticalityLabel, String incidentType, String involved, String location) {}

package com.mineguard.platform.monitoring.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Mobile safety-alert resource: {id, kind, title, description, elapsedLabel, primaryAction?}. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MobileAlertResource(String id, String kind, String title, String description,
                                  String elapsedLabel, String primaryAction) {
}

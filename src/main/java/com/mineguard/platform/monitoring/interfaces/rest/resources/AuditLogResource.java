package com.mineguard.platform.monitoring.interfaces.rest.resources;

import java.util.List;

/** Envelope expected by the web frontend: {entries: [...]}. */
public record AuditLogResource(List<AuditLogEntryResource> entries) {
}

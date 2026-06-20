package com.mineguard.platform.iam.interfaces.rest.resources;

import java.util.List;

/** Envelope expected by the web frontend: {supervisors: [...]}. */
public record SupervisorsResource(List<SupervisorResource> supervisors) {
}

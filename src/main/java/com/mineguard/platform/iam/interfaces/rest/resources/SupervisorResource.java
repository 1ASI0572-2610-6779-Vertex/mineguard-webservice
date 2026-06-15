package com.mineguard.platform.iam.interfaces.rest.resources;

/** Supervisor directory item resource. accessStatus is serialized lowercase. */
public record SupervisorResource(Long id, String fullName, String corporateId, String email, String accessStatus, Long userId) {}
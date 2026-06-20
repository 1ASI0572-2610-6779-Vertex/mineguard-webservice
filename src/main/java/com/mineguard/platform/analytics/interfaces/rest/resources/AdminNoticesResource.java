package com.mineguard.platform.analytics.interfaces.rest.resources;
import java.util.List;
/** Envelope expected by the web frontend: {notices: [...]}. */
public record AdminNoticesResource(List<AdminNoticeResource> notices) {}

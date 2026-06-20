package com.mineguard.platform.analytics.interfaces.rest.resources;
import java.util.Map;
public record AdminNoticeResource(Long id, String level, String i18nKey, Map<String,Object> i18nParams, String actionKey) {}

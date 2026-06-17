package com.mineguard.platform.analytics.interfaces.rest.transform;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mineguard.platform.analytics.domain.model.aggregates.AdminNotice;
import com.mineguard.platform.analytics.interfaces.rest.resources.AdminNoticeResource;

import java.util.Map;

public final class AdminNoticeResourceFromEntityAssembler {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private AdminNoticeResourceFromEntityAssembler() {
    }

    public static AdminNoticeResource toResourceFromEntity(AdminNotice d) {
        Map<String, Object> params = Map.of();
        if (d.getI18nParamsJson() != null && !d.getI18nParamsJson().isBlank()) {
            try {
                params = MAPPER.readValue(d.getI18nParamsJson(), new TypeReference<Map<String, Object>>() {
                });
            } catch (Exception ignored) {
                params = Map.of();
            }
        }
        return new AdminNoticeResource(d.getId(), d.getLevel(), d.getI18nKey(), params, d.getActionKey());
    }
}

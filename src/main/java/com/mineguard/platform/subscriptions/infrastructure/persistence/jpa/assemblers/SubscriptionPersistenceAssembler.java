package com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.assemblers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Subscription;
import com.mineguard.platform.subscriptions.domain.model.entities.Payment;
import com.mineguard.platform.subscriptions.domain.model.entities.PaymentMethod;
import com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.entities.SubscriptionPersistenceEntity;
import java.util.ArrayList;
import java.util.List;
public final class SubscriptionPersistenceAssembler {
    private static final ObjectMapper MAPPER = JsonMapper.builder().findAndAddModules().build();
    private static final TypeReference<List<Payment>> PAYMENTS_TYPE = new TypeReference<>() {};
    private SubscriptionPersistenceAssembler() {}
    public static Subscription toDomain(SubscriptionPersistenceEntity entity) {
        if (entity == null) return null;
        var subscription = new Subscription();
        subscription.setId(entity.getId());
        subscription.setCompanyId(entity.getCompanyId());
        subscription.setUserId(entity.getUserId());
        subscription.setPlan(readValue(entity.getPlanJson(), com.mineguard.platform.subscriptions.domain.model.entities.Plan.class));
        subscription.setStatus(entity.getStatus());
        subscription.setStartDate(entity.getStartDate());
        subscription.setEndDate(entity.getEndDate());
        subscription.setAutoRenew(entity.getAutoRenew());
        subscription.setPayments(readList(entity.getPaymentsJson()));
        subscription.setPaymentMethod(readValue(entity.getPaymentMethodJson(), PaymentMethod.class));
        return subscription;
    }
    public static SubscriptionPersistenceEntity toEntity(Subscription subscription) {
        var entity = new SubscriptionPersistenceEntity();
        entity.setId(subscription.getId());
        entity.setCompanyId(subscription.getCompanyId());
        entity.setUserId(subscription.getUserId());
        entity.setPlanJson(writeValue(subscription.getPlan()));
        entity.setStatus(subscription.getStatus());
        entity.setStartDate(subscription.getStartDate());
        entity.setEndDate(subscription.getEndDate());
        entity.setAutoRenew(subscription.getAutoRenew());
        entity.setPaymentsJson(writeValue(subscription.getPayments() != null ? subscription.getPayments() : new ArrayList<>()));
        entity.setPaymentMethodJson(writeValue(subscription.getPaymentMethod()));
        return entity;
    }
    private static <T> T readValue(String json, Class<T> type) {
        try {
            if (json == null || json.isBlank()) return null;
            return MAPPER.readValue(json, type);
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot read subscription JSON payload", exception);
        }
    }
    private static List<Payment> readList(String json) {
        try {
            if (json == null || json.isBlank()) return new ArrayList<>();
            return MAPPER.readValue(json, PAYMENTS_TYPE);
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot read subscription payments JSON payload", exception);
        }
    }
    private static String writeValue(Object value) {
        try {
            if (value == null) return null;
            return MAPPER.writeValueAsString(value);
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot write subscription JSON payload", exception);
        }
    }
}

package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.repositories;

import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertPriority;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertStatus;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertType;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities.AlertPersistenceEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AlertPersistenceRepositoryIntegrationTest {

    @Autowired
    private AlertPersistenceRepository repository;

    private String shortCode() {
        return "AL-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    void shouldSaveAlertSuccessfully() {
        AlertPersistenceEntity alert = new AlertPersistenceEntity();
        alert.setCode(shortCode());
        alert.setType(AlertType.FATIGUE);
        alert.setPriority(AlertPriority.CRITICAL);
        alert.setStatus(AlertStatus.ACTIVE);
        alert.setOccurredAt("2026-07-08T10:30:00");
        alert.setTitle("Fatigue detected");
        alert.setDescription("Driver shows fatigue signs");
        alert.setVehicleCode("VH-001");
        alert.setDriverName("Carlos Mendoza");
        alert.setCompanyId(1L);

        AlertPersistenceEntity saved = repository.saveAndFlush(alert);

        assertNotNull(saved.getId());
        assertEquals(AlertType.FATIGUE, saved.getType());
        assertEquals(AlertPriority.CRITICAL, saved.getPriority());
        assertEquals(AlertStatus.ACTIVE, saved.getStatus());
    }

    @Test
    void shouldFindAlertsByCompanyId() {
        Long companyId = 77L;

        AlertPersistenceEntity alert = new AlertPersistenceEntity();
        alert.setCode(shortCode());
        alert.setType(AlertType.PROXIMITY);
        alert.setPriority(AlertPriority.WARNING);
        alert.setStatus(AlertStatus.ACTIVE);
        alert.setOccurredAt("2026-07-08T10:35:00");
        alert.setTitle("Proximity alert");
        alert.setDescription("Vehicle proximity risk detected");
        alert.setVehicleCode("VH-002");
        alert.setDriverName("Luis Torres");
        alert.setCompanyId(companyId);

        repository.saveAndFlush(alert);

        List<AlertPersistenceEntity> result = repository.findAllByCompanyId(companyId);

        assertFalse(result.isEmpty());
        assertEquals(companyId, result.get(0).getCompanyId());
    }
}
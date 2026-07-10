package com.mineguard.platform.assets.infrastructure.persistence.jpa.repositories;

import com.mineguard.platform.assets.domain.model.valueobjects.ShiftStatus;
import com.mineguard.platform.assets.infrastructure.persistence.jpa.entities.DriverPersistenceEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DriverPersistenceRepositoryIntegrationTest {

    @Autowired
    private DriverPersistenceRepository repository;

    private String shortCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    void shouldSaveDriverSuccessfully() {
        DriverPersistenceEntity driver = new DriverPersistenceEntity();
        driver.setFullName("Carlos Mendoza");
        driver.setOperatorId("OP-" + shortCode());
        driver.setLicense("LIC-" + shortCode());
        driver.setSpecialty("Heavy Truck");
        driver.setShiftStatus(ShiftStatus.ON_SHIFT);
        driver.setCompanyId(1L);
        driver.setUserId(101L);

        DriverPersistenceEntity saved = repository.saveAndFlush(driver);

        assertNotNull(saved.getId());
        assertEquals("Carlos Mendoza", saved.getFullName());
        assertEquals(ShiftStatus.ON_SHIFT, saved.getShiftStatus());
    }

    @Test
    void shouldCountDriversByShiftStatus() {
        long initial = repository.countByShiftStatus(ShiftStatus.OFF_SHIFT);

        DriverPersistenceEntity driver = new DriverPersistenceEntity();
        driver.setFullName("Luis Torres");
        driver.setOperatorId("OP-" + shortCode());
        driver.setLicense("LIC-" + shortCode());
        driver.setSpecialty("Excavator");
        driver.setShiftStatus(ShiftStatus.OFF_SHIFT);
        driver.setCompanyId(2L);
        driver.setUserId(202L);

        repository.saveAndFlush(driver);

        assertEquals(initial + 1, repository.countByShiftStatus(ShiftStatus.OFF_SHIFT));
    }

    @Test
    void shouldFindDriversByCompanyIdAndUserId() {
        Long companyId = 88L;
        Long userId = 303L;

        DriverPersistenceEntity driver = new DriverPersistenceEntity();
        driver.setFullName("Miguel Rojas");
        driver.setOperatorId("OP-" + shortCode());
        driver.setLicense("LIC-" + shortCode());
        driver.setSpecialty("Loader");
        driver.setShiftStatus(ShiftStatus.INACTIVE);
        driver.setCompanyId(companyId);
        driver.setUserId(userId);

        repository.saveAndFlush(driver);

        List<DriverPersistenceEntity> byCompany = repository.findAllByCompanyId(companyId);
        Optional<DriverPersistenceEntity> byUser = repository.findByUserId(userId);

        assertFalse(byCompany.isEmpty());
        assertEquals(companyId, byCompany.get(0).getCompanyId());

        assertTrue(byUser.isPresent());
        assertEquals("Miguel Rojas", byUser.get().getFullName());
    }
}
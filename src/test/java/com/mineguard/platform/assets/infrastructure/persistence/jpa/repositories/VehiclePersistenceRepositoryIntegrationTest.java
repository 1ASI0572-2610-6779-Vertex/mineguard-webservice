package com.mineguard.platform.assets.infrastructure.persistence.jpa.repositories;

import com.mineguard.platform.assets.domain.model.valueobjects.VehicleStatus;
import com.mineguard.platform.assets.infrastructure.persistence.jpa.entities.VehiclePersistenceEntity;
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
class VehiclePersistenceRepositoryIntegrationTest {

    @Autowired
    private VehiclePersistenceRepository repository;

    private String shortCode() {
        return "VH-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    void shouldSaveVehicleSuccessfully() {
        VehiclePersistenceEntity vehicle = new VehiclePersistenceEntity();
        vehicle.setCode(shortCode());
        vehicle.setModel("Volvo FMX");
        vehicle.setCategory("Heavy Truck");
        vehicle.setStatus(VehicleStatus.OPERATIONAL);
        vehicle.setCompanyId(1L);
        vehicle.setArchived(false);

        VehiclePersistenceEntity saved = repository.saveAndFlush(vehicle);

        assertNotNull(saved.getId());
        assertEquals(VehicleStatus.OPERATIONAL, saved.getStatus());
        assertEquals("Volvo FMX", saved.getModel());
    }

    @Test
    void shouldCountVehiclesByStatus() {
        long initial = repository.countByStatus(VehicleStatus.MAINTENANCE);

        VehiclePersistenceEntity vehicle = new VehiclePersistenceEntity();
        vehicle.setCode(shortCode());
        vehicle.setModel("CAT 797F");
        vehicle.setCategory("Mining Truck");
        vehicle.setStatus(VehicleStatus.MAINTENANCE);
        vehicle.setCompanyId(2L);
        vehicle.setArchived(false);

        repository.saveAndFlush(vehicle);

        assertEquals(initial + 1, repository.countByStatus(VehicleStatus.MAINTENANCE));
    }

    @Test
    void shouldFindVehiclesByCompanyId() {
        Long companyId = 99L;

        VehiclePersistenceEntity vehicle = new VehiclePersistenceEntity();
        vehicle.setCode(shortCode());
        vehicle.setModel("Komatsu 930E");
        vehicle.setCategory("Haul Truck");
        vehicle.setStatus(VehicleStatus.IN_TRANSIT);
        vehicle.setCompanyId(companyId);
        vehicle.setArchived(false);

        repository.saveAndFlush(vehicle);

        List<VehiclePersistenceEntity> result = repository.findAllByCompanyId(companyId);

        assertFalse(result.isEmpty());
        assertEquals(companyId, result.get(0).getCompanyId());
    }
}
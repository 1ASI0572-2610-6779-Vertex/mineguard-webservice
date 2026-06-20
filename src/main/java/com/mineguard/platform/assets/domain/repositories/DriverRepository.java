package com.mineguard.platform.assets.domain.repositories;

import com.mineguard.platform.assets.domain.model.aggregates.Driver;

import java.util.List;
import java.util.Optional;

public interface DriverRepository {
    Driver save(Driver driver);
    Optional<Driver> findById(Long id);
    List<Driver> findAll();
    List<Driver> findAllByCompanyId(Long companyId);
    long count();
    long countByShiftStatus(com.mineguard.platform.assets.domain.model.valueobjects.ShiftStatus status);
    Optional<Driver> findByUserId(Long userId);
}

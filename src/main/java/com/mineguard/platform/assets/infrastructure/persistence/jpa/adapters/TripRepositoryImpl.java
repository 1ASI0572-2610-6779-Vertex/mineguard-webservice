package com.mineguard.platform.assets.infrastructure.persistence.jpa.adapters;

import com.mineguard.platform.assets.domain.model.aggregates.Trip;
import com.mineguard.platform.assets.domain.repositories.TripRepository;
import com.mineguard.platform.assets.infrastructure.persistence.jpa.assemblers.TripPersistenceAssembler;
import com.mineguard.platform.assets.infrastructure.persistence.jpa.repositories.TripPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TripRepositoryImpl implements TripRepository {
    private final TripPersistenceRepository repository;

    public TripRepositoryImpl(TripPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Trip save(Trip trip) {
        return TripPersistenceAssembler.toDomain(repository.save(TripPersistenceAssembler.toEntity(trip)));
    }

    @Override
    public List<Trip> findAll() {
        return repository.findAll().stream().map(TripPersistenceAssembler::toDomain).toList();
    }
}

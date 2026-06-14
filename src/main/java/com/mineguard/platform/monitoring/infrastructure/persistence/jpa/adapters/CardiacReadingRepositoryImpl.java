package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.adapters;

import com.mineguard.platform.monitoring.domain.model.aggregates.CardiacReading;
import com.mineguard.platform.monitoring.domain.repositories.CardiacReadingRepository;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.assemblers.CardiacReadingPersistenceAssembler;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.repositories.CardiacReadingPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CardiacReadingRepositoryImpl implements CardiacReadingRepository {
    private final CardiacReadingPersistenceRepository repository;

    public CardiacReadingRepositoryImpl(CardiacReadingPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public CardiacReading save(CardiacReading reading) {
        return CardiacReadingPersistenceAssembler.toDomain(repository.save(CardiacReadingPersistenceAssembler.toEntity(reading)));
    }

    @Override
    public List<CardiacReading> findAll() {
        return repository.findAll().stream().map(CardiacReadingPersistenceAssembler::toDomain).toList();
    }
}

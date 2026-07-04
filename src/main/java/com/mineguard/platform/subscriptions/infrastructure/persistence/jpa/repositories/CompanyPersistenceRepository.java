package com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.repositories;
import com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.entities.CompanyPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface CompanyPersistenceRepository extends JpaRepository<CompanyPersistenceEntity, Long> {
    Optional<CompanyPersistenceEntity> findByEdgeApiKey(String edgeApiKey);
}

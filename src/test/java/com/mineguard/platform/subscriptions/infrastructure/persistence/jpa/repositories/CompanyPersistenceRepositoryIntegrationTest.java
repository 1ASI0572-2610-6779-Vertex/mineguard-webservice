package com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.repositories;

import com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.entities.CompanyPersistenceEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CompanyPersistenceRepositoryIntegrationTest {

    @Autowired
    private CompanyPersistenceRepository repository;

    @Test
    void shouldSaveCompanySuccessfully() {
        CompanyPersistenceEntity company = new CompanyPersistenceEntity();
        company.setName("MineGuard Test");
        company.setEdgeApiKey("api-key-" + UUID.randomUUID());
        company.setSubscriptionPlan("STANDARD");

        CompanyPersistenceEntity saved = repository.saveAndFlush(company);

        assertNotNull(saved.getId());
        assertEquals("MineGuard Test", saved.getName());
        assertEquals("STANDARD", saved.getSubscriptionPlan());
    }

    @Test
    void shouldFindCompanyById() {
        CompanyPersistenceEntity company = new CompanyPersistenceEntity();
        company.setName("Company By Id Test");
        company.setEdgeApiKey("api-key-" + UUID.randomUUID());
        company.setSubscriptionPlan("STANDARD");

        CompanyPersistenceEntity saved = repository.saveAndFlush(company);

        Optional<CompanyPersistenceEntity> result = repository.findById(saved.getId());

        assertTrue(result.isPresent());
        assertEquals("Company By Id Test", result.get().getName());
    }

    @Test
    void shouldFindCompanyByEdgeApiKey() {
        String apiKey = "api-key-" + UUID.randomUUID();

        CompanyPersistenceEntity company = new CompanyPersistenceEntity();
        company.setName("Company Api Key Test");
        company.setEdgeApiKey(apiKey);
        company.setSubscriptionPlan("STANDARD");

        repository.saveAndFlush(company);

        Optional<CompanyPersistenceEntity> result = repository.findByEdgeApiKey(apiKey);

        assertTrue(result.isPresent());
        assertEquals("Company Api Key Test", result.get().getName());
    }
}
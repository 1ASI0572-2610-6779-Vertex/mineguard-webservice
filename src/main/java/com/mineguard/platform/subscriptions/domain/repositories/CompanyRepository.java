package com.mineguard.platform.subscriptions.domain.repositories;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Company;
import java.util.List;
import java.util.Optional;
public interface CompanyRepository {
    Company save(Company c);
    Optional<Company> findById(Long id);
    List<Company> findAll();
    long count();
    Optional<Company> findByEdgeApiKey(String edgeApiKey);
}

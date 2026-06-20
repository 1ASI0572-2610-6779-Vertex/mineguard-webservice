package com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.adapters;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Company;
import com.mineguard.platform.subscriptions.domain.repositories.CompanyRepository;
import com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.assemblers.CompanyPersistenceAssembler;
import com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.repositories.CompanyPersistenceRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public class CompanyRepositoryImpl implements CompanyRepository {
    private final CompanyPersistenceRepository r;
    public CompanyRepositoryImpl(CompanyPersistenceRepository r){this.r=r;}
    public Company save(Company c){return CompanyPersistenceAssembler.toDomain(r.save(CompanyPersistenceAssembler.toEntity(c)));}
    public List<Company> findAll(){return r.findAll().stream().map(CompanyPersistenceAssembler::toDomain).toList();}
    public long count(){return r.count();}
}

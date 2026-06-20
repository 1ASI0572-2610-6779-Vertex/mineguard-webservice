package com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.assemblers;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Company;
import com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.entities.CompanyPersistenceEntity;
public final class CompanyPersistenceAssembler {
    private CompanyPersistenceAssembler(){}
    public static Company toDomain(CompanyPersistenceEntity e){ if(e==null)return null; var c=new Company(e.getName()); c.setId(e.getId()); return c; }
    public static CompanyPersistenceEntity toEntity(Company c){ var e=new CompanyPersistenceEntity(); e.setId(c.getId()); e.setName(c.getName()); return e; }
}

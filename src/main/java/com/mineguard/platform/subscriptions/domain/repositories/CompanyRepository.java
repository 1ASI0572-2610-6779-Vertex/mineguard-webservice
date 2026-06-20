package com.mineguard.platform.subscriptions.domain.repositories;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Company;
import java.util.List;
public interface CompanyRepository { Company save(Company c); List<Company> findAll(); long count(); }

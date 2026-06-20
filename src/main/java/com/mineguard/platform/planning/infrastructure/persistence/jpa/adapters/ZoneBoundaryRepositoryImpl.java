package com.mineguard.platform.planning.infrastructure.persistence.jpa.adapters;
import com.mineguard.platform.planning.domain.model.aggregates.ZoneBoundary;
import com.mineguard.platform.planning.domain.repositories.ZoneBoundaryRepository;
import com.mineguard.platform.planning.infrastructure.persistence.jpa.assemblers.ZoneBoundaryPersistenceAssembler;
import com.mineguard.platform.planning.infrastructure.persistence.jpa.repositories.ZoneBoundaryPersistenceRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public class ZoneBoundaryRepositoryImpl implements ZoneBoundaryRepository {
    private final ZoneBoundaryPersistenceRepository r;
    public ZoneBoundaryRepositoryImpl(ZoneBoundaryPersistenceRepository r){this.r=r;}
    public ZoneBoundary save(ZoneBoundary b){return ZoneBoundaryPersistenceAssembler.toDomain(r.save(ZoneBoundaryPersistenceAssembler.toEntity(b)));}
    public List<ZoneBoundary> findAll(){return r.findAll().stream().map(ZoneBoundaryPersistenceAssembler::toDomain).toList();}
}

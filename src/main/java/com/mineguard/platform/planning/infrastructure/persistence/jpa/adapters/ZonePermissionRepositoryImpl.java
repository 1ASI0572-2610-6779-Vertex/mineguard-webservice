package com.mineguard.platform.planning.infrastructure.persistence.jpa.adapters;
import com.mineguard.platform.planning.domain.model.aggregates.ZonePermission;
import com.mineguard.platform.planning.domain.repositories.ZonePermissionRepository;
import com.mineguard.platform.planning.infrastructure.persistence.jpa.assemblers.ZonePermissionPersistenceAssembler;
import com.mineguard.platform.planning.infrastructure.persistence.jpa.repositories.ZonePermissionPersistenceRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public class ZonePermissionRepositoryImpl implements ZonePermissionRepository {
    private final ZonePermissionPersistenceRepository r;
    public ZonePermissionRepositoryImpl(ZonePermissionPersistenceRepository r){this.r=r;}
    public ZonePermission save(ZonePermission p){return ZonePermissionPersistenceAssembler.toDomain(r.save(ZonePermissionPersistenceAssembler.toEntity(p)));}
    public List<ZonePermission> findAll(){return r.findAll().stream().map(ZonePermissionPersistenceAssembler::toDomain).toList();}
}

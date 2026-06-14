package com.mineguard.platform.planning.infrastructure.persistence.jpa.adapters;
import com.mineguard.platform.planning.domain.model.aggregates.GeofenceZone;
import com.mineguard.platform.planning.domain.repositories.GeofenceZoneRepository;
import com.mineguard.platform.planning.infrastructure.persistence.jpa.assemblers.GeofenceZonePersistenceAssembler;
import com.mineguard.platform.planning.infrastructure.persistence.jpa.repositories.GeofenceZonePersistenceRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public class GeofenceZoneRepositoryImpl implements GeofenceZoneRepository {
    private final GeofenceZonePersistenceRepository r;
    public GeofenceZoneRepositoryImpl(GeofenceZonePersistenceRepository r){this.r=r;}
    public GeofenceZone save(GeofenceZone z){return GeofenceZonePersistenceAssembler.toDomain(r.save(GeofenceZonePersistenceAssembler.toEntity(z)));}
    public List<GeofenceZone> findAll(){return r.findAll().stream().map(GeofenceZonePersistenceAssembler::toDomain).toList();}
    public long count(){return r.count();}
}

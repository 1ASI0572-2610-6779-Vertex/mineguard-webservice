package com.mineguard.platform.planning.infrastructure.persistence.jpa.assemblers;
import com.mineguard.platform.planning.domain.model.aggregates.GeofenceZone;
import com.mineguard.platform.planning.infrastructure.persistence.jpa.entities.GeofenceZonePersistenceEntity;
public final class GeofenceZonePersistenceAssembler {
    private GeofenceZonePersistenceAssembler(){}
    public static GeofenceZone toDomain(GeofenceZonePersistenceEntity e){ if(e==null)return null; var z=new GeofenceZone(e.getZoneName(),e.getZoneType(),e.getStatus()); z.setId(e.getId()); return z; }
    public static GeofenceZonePersistenceEntity toEntity(GeofenceZone z){ var e=new GeofenceZonePersistenceEntity(); e.setId(z.getId()); e.setZoneName(z.getZoneName()); e.setZoneType(z.getZoneType()); e.setStatus(z.getStatus()); return e; }
}

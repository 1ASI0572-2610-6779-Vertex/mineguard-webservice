package com.mineguard.platform.planning.infrastructure.persistence.jpa.assemblers;
import com.mineguard.platform.planning.domain.model.aggregates.ZoneBoundary;
import com.mineguard.platform.planning.infrastructure.persistence.jpa.entities.ZoneBoundaryPersistenceEntity;
public final class ZoneBoundaryPersistenceAssembler {
    private ZoneBoundaryPersistenceAssembler(){}
    public static ZoneBoundary toDomain(ZoneBoundaryPersistenceEntity e){ if(e==null)return null; var b=new ZoneBoundary(e.getZoneId(),e.getLatitude(),e.getLongitude(),e.getPointOrder()); b.setId(e.getId()); return b; }
    public static ZoneBoundaryPersistenceEntity toEntity(ZoneBoundary b){ var e=new ZoneBoundaryPersistenceEntity(); e.setId(b.getId()); e.setZoneId(b.getZoneId()); e.setLatitude(b.getLatitude()); e.setLongitude(b.getLongitude()); e.setPointOrder(b.getPointOrder()); return e; }
}

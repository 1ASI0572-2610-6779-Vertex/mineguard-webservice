package com.mineguard.platform.planning.infrastructure.persistence.jpa.assemblers;
import com.mineguard.platform.planning.domain.model.aggregates.ZonePermission;
import com.mineguard.platform.planning.infrastructure.persistence.jpa.entities.ZonePermissionPersistenceEntity;
public final class ZonePermissionPersistenceAssembler {
    private ZonePermissionPersistenceAssembler(){}
    public static ZonePermission toDomain(ZonePermissionPersistenceEntity e){ if(e==null)return null; var p=new ZonePermission(e.getZoneId(),e.getDriverId(),e.getPermissionType(),e.getStartDate(),e.getEndDate(),e.getStatus()); p.setId(e.getId()); return p; }
    public static ZonePermissionPersistenceEntity toEntity(ZonePermission p){ var e=new ZonePermissionPersistenceEntity(); e.setId(p.getId()); e.setZoneId(p.getZoneId()); e.setDriverId(p.getDriverId()); e.setPermissionType(p.getPermissionType()); e.setStartDate(p.getStartDate()); e.setEndDate(p.getEndDate()); e.setStatus(p.getStatus()); return e; }
}

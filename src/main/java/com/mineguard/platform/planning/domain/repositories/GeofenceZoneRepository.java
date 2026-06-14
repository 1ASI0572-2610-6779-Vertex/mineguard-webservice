package com.mineguard.platform.planning.domain.repositories;
import com.mineguard.platform.planning.domain.model.aggregates.GeofenceZone;
import java.util.List;
public interface GeofenceZoneRepository { GeofenceZone save(GeofenceZone z); List<GeofenceZone> findAll(); long count(); }

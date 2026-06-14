package com.mineguard.platform.planning.domain.repositories;
import com.mineguard.platform.planning.domain.model.aggregates.ZonePermission;
import java.util.List;
public interface ZonePermissionRepository { ZonePermission save(ZonePermission p); List<ZonePermission> findAll(); }

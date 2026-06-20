package com.mineguard.platform.planning.domain.repositories;
import com.mineguard.platform.planning.domain.model.aggregates.ZoneBoundary;
import java.util.List;
public interface ZoneBoundaryRepository { ZoneBoundary save(ZoneBoundary b); List<ZoneBoundary> findAll(); }

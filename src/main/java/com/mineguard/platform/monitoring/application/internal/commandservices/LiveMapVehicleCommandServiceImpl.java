package com.mineguard.platform.monitoring.application.internal.commandservices;

import com.mineguard.platform.monitoring.application.commandservices.LiveMapVehicleCommandService;
import com.mineguard.platform.monitoring.domain.model.aggregates.LiveMapVehicle;
import com.mineguard.platform.monitoring.domain.model.commands.UpdateVehicleLocationCommand;
import com.mineguard.platform.monitoring.domain.repositories.LiveMapVehicleRepository;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

/** Updates the GPS position of a live-map vehicle marker. Creates the marker if absent. */
@Service
public class LiveMapVehicleCommandServiceImpl implements LiveMapVehicleCommandService {

    private final LiveMapVehicleRepository liveMapVehicleRepository;

    public LiveMapVehicleCommandServiceImpl(LiveMapVehicleRepository liveMapVehicleRepository) {
        this.liveMapVehicleRepository = liveMapVehicleRepository;
    }

    @Override
    public Result<LiveMapVehicle, ApplicationError> handle(UpdateVehicleLocationCommand command) {
        var existing = liveMapVehicleRepository.findByVehicleId(command.vehicleId());
        LiveMapVehicle marker;
        if (existing.isPresent()) {
            marker = existing.get();
            marker.setLatitude(command.lat());
            marker.setLongitude(command.lng());
        } else {
            marker = new LiveMapVehicle();
            marker.setVehicleId(command.vehicleId());
            marker.setLatitude(command.lat());
            marker.setLongitude(command.lng());
            marker.setStatus("active");
        }
        return Result.success(liveMapVehicleRepository.save(marker));
    }
}

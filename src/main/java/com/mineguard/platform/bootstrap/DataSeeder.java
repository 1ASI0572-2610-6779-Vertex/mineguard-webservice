package com.mineguard.platform.bootstrap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mineguard.platform.analytics.infrastructure.persistence.jpa.entities.*;
import com.mineguard.platform.analytics.infrastructure.persistence.jpa.repositories.*;
import com.mineguard.platform.assets.domain.model.aggregates.CatalogSummary;
import com.mineguard.platform.assets.domain.model.aggregates.Driver;
import com.mineguard.platform.assets.domain.model.aggregates.Trip;
import com.mineguard.platform.assets.domain.model.aggregates.Vehicle;
import com.mineguard.platform.assets.domain.model.valueobjects.ShiftStatus;
import com.mineguard.platform.assets.domain.model.valueobjects.TripStatus;
import com.mineguard.platform.assets.domain.model.valueobjects.VehicleStatus;
import com.mineguard.platform.assets.domain.repositories.*;
import com.mineguard.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.mineguard.platform.iam.domain.model.aggregates.Supervisor;
import com.mineguard.platform.iam.domain.model.aggregates.User;
import com.mineguard.platform.iam.domain.model.entities.Role;
import com.mineguard.platform.iam.domain.model.valueobjects.AccessStatus;
import com.mineguard.platform.iam.domain.model.valueobjects.Roles;
import com.mineguard.platform.iam.domain.repositories.DeviceRepository;
import com.mineguard.platform.iam.domain.repositories.RoleRepository;
import com.mineguard.platform.iam.domain.repositories.SupervisorRepository;
import com.mineguard.platform.iam.domain.repositories.UserRepository;
import com.mineguard.platform.iam.interfaces.acl.IamContextFacade;
import com.mineguard.platform.monitoring.domain.model.aggregates.*;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertPriority;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertStatus;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertType;
import com.mineguard.platform.monitoring.domain.repositories.*;
import com.mineguard.platform.planning.domain.model.aggregates.GeofenceZone;
import com.mineguard.platform.planning.domain.model.aggregates.ZoneBoundary;
import com.mineguard.platform.planning.domain.model.aggregates.ZonePermission;
import com.mineguard.platform.planning.domain.repositories.GeofenceZoneRepository;
import com.mineguard.platform.planning.domain.repositories.ZoneBoundaryRepository;
import com.mineguard.platform.planning.domain.repositories.ZonePermissionRepository;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Company;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Plan;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Subscription;
import com.mineguard.platform.subscriptions.domain.repositories.CompanyRepository;
import com.mineguard.platform.subscriptions.domain.repositories.PlanRepository;
import com.mineguard.platform.subscriptions.domain.repositories.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

/**
 * Seeds the platform database from the bundled MineGuard dataset
 * ({@code seed/db.json}) so both frontends and the edge bridge have data to work
 * with on startup. Runs after IAM role seeding (Order 2) and is idempotent.
 */
@Component
public class DataSeeder {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SupervisorRepository supervisorRepository;
    private final HashingService hashingService;
    private final IamContextFacade iamContextFacade;
    private final CompanyRepository companyRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final TripRepository tripRepository;
    private final AlertRepository alertRepository;
    private final SensorRepository sensorRepository;
    private final SensorReadingRepository sensorReadingRepository;
    private final IncidentRepository incidentRepository;
    private final LiveMapVehicleRepository liveMapVehicleRepository;
    private final AuditLogEntryRepository auditLogEntryRepository;
    private final GeofenceZoneRepository geofenceZoneRepository;
    private final ZoneBoundaryRepository zoneBoundaryRepository;
    private final ZonePermissionRepository zonePermissionRepository;
    private final PerformanceMetricPersistenceRepository performanceMetricRepo;
    private final ReportPersistenceRepository reportRepo;

    @Value("${edge.device.default.device-id}")
    private String defaultDeviceId;
    @Value("${edge.device.default.api-key}")
    private String defaultApiKey;

    private final ObjectMapper mapper = new ObjectMapper();

    public DataSeeder(UserRepository userRepository, RoleRepository roleRepository,
                      SupervisorRepository supervisorRepository, HashingService hashingService,
                      IamContextFacade iamContextFacade, CompanyRepository companyRepository,
                      PlanRepository planRepository, SubscriptionRepository subscriptionRepository,
                      DriverRepository driverRepository, VehicleRepository vehicleRepository,
                      TripRepository tripRepository,
                      AlertRepository alertRepository, SensorRepository sensorRepository,
                      SensorReadingRepository sensorReadingRepository, IncidentRepository incidentRepository,
                      LiveMapVehicleRepository liveMapVehicleRepository, AuditLogEntryRepository auditLogEntryRepository,
                      GeofenceZoneRepository geofenceZoneRepository, ZoneBoundaryRepository zoneBoundaryRepository,
                      ZonePermissionRepository zonePermissionRepository,
                      PerformanceMetricPersistenceRepository performanceMetricRepo,
                      ReportPersistenceRepository reportRepo) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.supervisorRepository = supervisorRepository;
        this.hashingService = hashingService;
        this.iamContextFacade = iamContextFacade;
        this.companyRepository = companyRepository;
        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.tripRepository = tripRepository;
        this.alertRepository = alertRepository;
        this.sensorRepository = sensorRepository;
        this.sensorReadingRepository = sensorReadingRepository;
        this.incidentRepository = incidentRepository;
        this.liveMapVehicleRepository = liveMapVehicleRepository;
        this.auditLogEntryRepository = auditLogEntryRepository;
        this.geofenceZoneRepository = geofenceZoneRepository;
        this.zoneBoundaryRepository = zoneBoundaryRepository;
        this.zonePermissionRepository = zonePermissionRepository;
        this.performanceMetricRepo = performanceMetricRepo;
        this.reportRepo = reportRepo;
    }

    @Order(2)
    @EventListener(ApplicationReadyEvent.class)
    public void seed(ApplicationReadyEvent event) {
        if (userRepository.existsByUsername("admin_mineguard")) {
            LOGGER.info("Seed data already present; skipping.");
            return;
        }
        try {
            JsonNode db;
            try (InputStream in = new ClassPathResource("seed/db.json").getInputStream()) {
                db = mapper.readTree(in);
            }
            seedIam(db);
            seedSubscriptions(db);
            seedAssets(db);
            seedMonitoring(db);
            seedPlanning(db);
            seedAnalytics(db);
            seedDevice();
            LOGGER.info("MineGuard seed data loaded successfully.");
        } catch (Exception e) {
            LOGGER.error("Failed to load seed data: {}", e.getMessage(), e);
        }
    }

    private Roles roleFromLabel(String label) {
        return switch (label == null ? "" : label.toLowerCase()) {
            case "administrator", "admin" -> Roles.ROLE_ADMINISTRATOR;
            case "supervisor" -> Roles.ROLE_SUPERVISOR;
            case "driver" -> Roles.ROLE_DRIVER;
            default -> Roles.ROLE_OPERATOR;
        };
    }

    private void seedIam(JsonNode db) {
        var roleByExternalId = new java.util.HashMap<Integer, Roles>();
        for (JsonNode r : db.path("userRoles")) {
            roleByExternalId.put(r.path("id").asInt(), roleFromLabel(r.path("role").asText()));
        }
        for (JsonNode u : db.path("users")) {
            var roleEnum = roleByExternalId.getOrDefault(u.path("id_role").asInt(), Roles.ROLE_OPERATOR);
            var role = roleRepository.findByName(roleEnum).orElseGet(() -> roleRepository.save(new Role(roleEnum)));
            var user = new User(u.path("username").asText(), hashingService.encode(u.path("password").asText("123456")),
                    u.path("email").asText(null), u.path("fullName").asText(null),
                    u.path("id_company").isMissingNode() ? null : u.path("id_company").asLong(), List.of(role));
            userRepository.save(user);
        }
        // Demo operator users so the mobile app can sign in by operator id
        var operatorRole = roleRepository.findByName(Roles.ROLE_OPERATOR)
                .orElseGet(() -> roleRepository.save(new Role(Roles.ROLE_OPERATOR)));
        for (JsonNode d : db.path("driversDirectory")) {
            var workerId = d.path("operatorId").asText(null);
            if (workerId != null && !userRepository.existsByUsername(workerId)) {
                userRepository.save(new User(workerId, hashingService.encode("123456"),
                        null, d.path("fullName").asText(null), List.of(operatorRole)));
            }
        }
        for (JsonNode s : db.path("supervisors")) {
            var supervisorRole = roleRepository.findByName(Roles.ROLE_SUPERVISOR)
                    .orElseGet(() -> roleRepository.save(new Role(Roles.ROLE_SUPERVISOR)));
            var username = s.path("email").asText(s.path("corporateId").asText()).split("@")[0];
            var user = userRepository.findByUsername(username)
                    .orElseGet(() -> userRepository.save(new User(username, hashingService.encode("123456"),
                            s.path("email").asText(null), s.path("fullName").asText(null), null, List.of(supervisorRole))));
            supervisorRepository.save(new Supervisor(user.getId(), s.path("fullName").asText(), s.path("corporateId").asText(),
                    s.path("email").asText(null), AccessStatus.fromSerialized(s.path("accessStatus").asText("active"))));
        }
    }

    private void seedSubscriptions(JsonNode db) {
        for (JsonNode c : db.path("companies")) companyRepository.save(new Company(c.path("name").asText()));
        for (JsonNode p : db.path("plans"))
            planRepository.save(new Plan(p.path("name").asText(), p.path("price").asDouble(), p.path("duration_days").asInt()));
        for (JsonNode s : db.path("subscriptions"))
            subscriptionRepository.save(new Subscription(s.path("id_company").asLong(), s.path("id_plan").asLong(),
                    s.path("start_date").asText(null), s.path("end_date").asText(null), s.path("status").asText(null)));
    }

    private void seedAssets(JsonNode db) {
        var userNamesById = new java.util.HashMap<Long, String>();
        for (JsonNode u : db.path("users")) {
            userNamesById.put(u.path("id").asLong(), u.path("fullName").asText(null));
        }
        for (JsonNode d : db.path("drivers")) {
            var fullName = userNamesById.getOrDefault(d.path("id_user").asLong(), "Driver " + d.path("id").asLong());
            driverRepository.save(new Driver(fullName, "OP-" + d.path("license_number").asText("").replaceAll("[^0-9]", ""),
                    d.path("license_number").asText(null), d.path("work_shift").asText(null),
                    ShiftStatus.fromSerialized("on_shift"), null, d.path("id_user").asLong()));
        }
        for (JsonNode v : db.path("vehicles")) {
            vehicleRepository.save(new Vehicle(v.path("id_driver").isNull() ? null : v.path("id_driver").asLong(),
                    v.path("plate_code").asText(null), v.path("vehicle_type").asText(null),
                    VehicleStatus.fromSerialized(v.path("status").asText("operational"))));
        }
        for (JsonNode t : db.path("trips"))
            tripRepository.save(new Trip(t.path("id_driver").asLong(), t.path("id_vehicle").asLong(),
                    t.path("start_time").asText(null), t.path("end_time").asText(null),
                    TripStatus.fromSerialized(t.path("status").asText("completed"))));
    }

    private void seedMonitoring(JsonNode db) {
        for (JsonNode a : db.path("alerts"))
            alertRepository.save(new Alert(a.path("id_trip").asLong(), a.path("id_sensor").asLong(),
                    a.path("alert_type").asText(null), a.path("severity").asText(null),
                    AlertStatus.fromSerialized(a.path("alert_status").asText("active")),
                    a.path("created_at").asText(null)));
        for (JsonNode s : db.path("sensors"))
            sensorRepository.save(new Sensor(s.path("id_vehicle").asLong(), s.path("sensor_type").asText(null), s.path("status").asText(null)));
        for (JsonNode r : db.path("sensorReadings"))
            sensorReadingRepository.save(new SensorReading(r.path("id_sensor").asLong(), r.path("reading_type").asText(null),
                    r.path("value").asDouble(), r.path("timestamp").asText(null)));
        for (JsonNode i : db.path("incidents"))
            incidentRepository.save(new Incident(i.path("id_alert").asLong(), i.path("description").asText(null),
                    i.path("incident_date").asText(null), i.path("status").asText(null), i.path("severity").asText(null)));
        for (JsonNode v : db.path("liveMapVehicles"))
            liveMapVehicleRepository.save(new LiveMapVehicle(v.path("code").asText(null), v.path("vehicleType").asText(null),
                    v.path("latitude").asDouble(), v.path("longitude").asDouble(), v.path("status").asText(null), v.path("driverName").asText(null)));
        for (JsonNode e : db.path("auditLog").path("entries"))
            auditLogEntryRepository.save(new AuditLogEntry(e.path("category").asText(null), e.path("occurredAt").asText(null),
                    e.path("titleKey").asText(null), e.path("descriptionKey").asText(null),
                    e.path("descriptionParams").toString(), e.path("actorKey").asText(null)));
    }

    private void seedPlanning(JsonNode db) {
        for (JsonNode z : db.path("geofenceZones"))
            geofenceZoneRepository.save(new GeofenceZone(z.path("zone_name").asText(null), z.path("zone_type").asText(null), z.path("status").asText(null)));
        for (JsonNode b : db.path("zoneBoundaries"))
            zoneBoundaryRepository.save(new ZoneBoundary(b.path("id_zone").asLong(), b.path("latitude").asDouble(),
                    b.path("longitude").asDouble(), b.path("point_order").asInt()));
        for (JsonNode p : db.path("zonePermissions"))
            zonePermissionRepository.save(new ZonePermission(p.path("id_zone").asLong(), p.path("id_driver").asLong(),
                    p.path("permission_type").asText(null), p.path("start_date").asText(null), p.path("end_date").asText(null), p.path("status").asText(null)));
    }

    private void seedAnalytics(JsonNode db) {
        for (JsonNode m : db.path("performanceMetrics")) {
            var e = new PerformanceMetricPersistenceEntity();
            e.setDriverId(m.path("id_driver").asLong()); e.setTripId(m.path("id_trip").asLong()); e.setVehicleId(m.path("id_vehicle").asLong());
            e.setFatigueEvents(m.path("fatigue_events").asInt()); e.setAlertsCount(m.path("alerts_count").asInt());
            e.setAverageHeartRate(m.path("average_heart_rate").asDouble()); e.setRiskScore(m.path("risk_score").asDouble());
            e.setCalculatedAt(m.path("calculated_at").asText(null));
            performanceMetricRepo.save(e);
        }
        for (JsonNode r : db.path("reports")) {
            var e = new ReportPersistenceEntity();
            e.setIncidentId(r.path("id_incident").asLong()); e.setAlertId(r.path("id_alert").asLong());
            e.setUserId(r.path("id_user").asLong()); e.setMetricId(r.path("id_metric").asLong());
            e.setReportType(r.path("report_type").asText(null)); e.setGeneratedAt(r.path("created_at").asText(null));
            e.setDescription(r.path("description").asText(null));
            reportRepo.save(e);
        }
    }

    private void seedDevice() {
        var driverId = driverRepository.findAll().stream().findFirst().map(Driver::getId).orElse(null);
        iamContextFacade.registerDevice(defaultDeviceId, defaultApiKey, driverId);
    }
}

Feature: Sprint 3 - MineGuard backend validation
  As a MineGuard user
  I want the backend to correctly manage companies, drivers, vehicles, sessions, telemetry and alerts
  So that mining operations can be monitored safely and reliably

  Scenario: Register a mining company
    Given a mining company provides its registration information
    When the company registration request is processed
    Then the system should create the company
    And the system should generate administrator credentials
    And the system should generate an API key for IoT telemetry

  Scenario: Create a driver for a company
    Given an administrator belongs to a registered company
    When the administrator creates a new driver
    Then the system should store the driver information
    And the system should associate the driver with the company
    And the system should generate access credentials for the driver

  Scenario: Start a driving session
    Given a driver belongs to a registered company
    And a vehicle is operational
    When the driver starts a driving session with that vehicle
    Then the system should create an active driving session
    And the vehicle should be marked as in transit

  Scenario: Prevent duplicated active driving sessions
    Given a driver already has an active driving session
    When the driver attempts to start another driving session
    Then the system should reject the request
    And the system should keep only one active driving session for the driver

  Scenario: Process critical IoT telemetry
    Given an IoT device sends telemetry using a valid API key
    And the telemetry contains critical heart rate or proximity values
    When the backend processes the telemetry
    Then the system should store the telemetry event
    And the system should generate an operational alert

  Scenario: Resolve an operational alert
    Given a supervisor reviews an active operational alert
    When the supervisor resolves the alert
    Then the system should update the alert status to resolved
    And the system should keep the alert associated with the company and vehicle
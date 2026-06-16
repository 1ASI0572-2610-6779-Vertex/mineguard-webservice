package com.mineguard.platform.iam.infrastructure.authorization.sfs.configuration;

import com.mineguard.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import com.mineguard.platform.iam.infrastructure.tokens.jwt.pipeline.BearerAuthorizationRequestFilter;
import com.mineguard.platform.iam.infrastructure.tokens.jwt.pipeline.UnauthorizedRequestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/** Stateless Spring Security configuration with JWT bearer authentication. */
@Configuration
public class WebSecurityConfiguration {

    private final UserDetailsService userDetailsService;
    private final BearerTokenService tokenService;
    private final UnauthorizedRequestHandler unauthorizedRequestHandler;

    public WebSecurityConfiguration(UserDetailsService userDetailsService,
                                    BearerTokenService tokenService,
                                    UnauthorizedRequestHandler unauthorizedRequestHandler) {
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
        this.unauthorizedRequestHandler = unauthorizedRequestHandler;
    }

    @Bean
    public BearerAuthorizationRequestFilter authorizationRequestFilter() {
        return new BearerAuthorizationRequestFilter(tokenService, userDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedRequestHandler))
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/authentication/**",
                                "/auth/**",
                                "/api/v1/authentication/**",
                                "/api/v1/health-monitoring/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/h2-console/**",
                                "/error"
                        ).permitAll()
                        // Read-only view-model endpoints polled by the frontends (dev-friendly, no token required)
                        .requestMatchers(org.springframework.http.HttpMethod.GET,
                                "/supervisors/**", "/administrators/**", "/vehiclesInventory/**", "/driversDirectory/**",
                                "/vehicles/**", "/operationalAlerts/**", "/auditLog/**", "/cardiacReadings/**",
                                "/fleetSummary/**", "/liveMapVehicles/**", "/alerts/**", "/performance/**",
                                "/dashboardSummary/**", "/dashboardTrend/**", "/dashboardRiskDrivers/**",
                                "/dashboardRecentAlerts/**", "/performanceMetrics/**", "/reports/**",
                                "/analyticsFatigueBars/**", "/analyticsIncidentDistribution/**",
                                "/analyticsHistoryRows/**", "/analyticsInsights/**", "/adminSummary/**", "/adminNotices/**"
                        ).permitAll()
                        .anyRequest().authenticated());
        http.headers(headers -> headers.frameOptions(frame -> frame.disable())); // for H2 console
        http.addFilterBefore(authorizationRequestFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

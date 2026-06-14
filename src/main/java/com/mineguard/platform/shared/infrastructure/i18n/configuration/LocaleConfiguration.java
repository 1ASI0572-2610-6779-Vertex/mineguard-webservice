package com.mineguard.platform.shared.infrastructure.i18n.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

/**
 * Configures locale resolution from the {@code Accept-Language} header.
 * Supported locales: English (default) and Spanish.
 */
@Configuration
public class LocaleConfiguration {

    /**
     * Resolves the request locale from the {@code Accept-Language} header.
     *
     * @return the configured {@link LocaleResolver}
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setSupportedLocales(List.of(Locale.ENGLISH, Locale.forLanguageTag("es")));
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }
}

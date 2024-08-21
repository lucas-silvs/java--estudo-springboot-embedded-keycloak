package com.lucassilvs.springbootkeycloak.config.providers.realmprovider.custom;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomUserResourceFactory implements RealmResourceProviderFactory {

    private static final Logger log = LoggerFactory.getLogger(CustomUserResourceFactory.class);

    @Override
    public RealmResourceProvider create(KeycloakSession keycloakSession) {
        return new CustomUserResourceProvider(keycloakSession);
    }

    @Override
    public void init(Config.Scope scope) {
        // Não implementando devido a não customização do init
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        log.info("-----------------------------------");
        log.info("Iniciando Rest Custom Provider");
        log.info("-----------------------------------");
    }

    @Override
    public void close() {
        // Não implementando devido a não customização do close
    }

    @Override
    public String getId() {
        return "customUserResourceRestFactory";
    }
}

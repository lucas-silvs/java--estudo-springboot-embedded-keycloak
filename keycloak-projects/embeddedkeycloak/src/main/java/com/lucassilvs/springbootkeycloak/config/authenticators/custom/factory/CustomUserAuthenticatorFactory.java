package com.lucassilvs.springbootkeycloak.config.authenticators.custom.factory;

import com.lucassilvs.springbootkeycloak.config.authenticators.custom.CustomAuthenticator;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

public class CustomUserAuthenticatorFactory implements AuthenticatorFactory {
    @Override
    public String getDisplayType() {
        return "custom-user-autenticator-factory";
    }

    @Override
    public String getReferenceCategory() {
        return "";
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return AuthenticationExecutionModel.Requirement.values();
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "autenticador para teste";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return List.of();
    }

    @Override
    public Authenticator create(KeycloakSession keycloakSession) {
        System.out.println("--------------Iniciando CUstom Authenticator Rapaz --------------------------");
        return new CustomAuthenticator();
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return "custom-user-autenticator-factory";
    }
}

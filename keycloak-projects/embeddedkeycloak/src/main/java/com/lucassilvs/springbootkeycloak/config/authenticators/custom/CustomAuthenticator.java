package com.lucassilvs.springbootkeycloak.config.authenticators.custom;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;

public class CustomAuthenticator implements Authenticator {
    @Override
    public void authenticate(AuthenticationFlowContext authenticationFlowContext) {

        UserModel user = authenticationFlowContext.getUser();

        // Realiza a validação personalizada do usuário
        if (user == null || !isValidUser(user)) {
            authenticationFlowContext.forkWithErrorMessage(new FormMessage("invalid_user_message"));
            return;
        }

        // Se a validação for bem-sucedida, avance no fluxo de autenticação
        authenticationFlowContext.success();

    }

    private boolean isValidUser(UserModel user) {

        return !user.getUsername().equals("23634689889");

    }

    @Override
    public void action(AuthenticationFlowContext authenticationFlowContext) {

    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {

    }

    @Override
    public void close() {

    }
}

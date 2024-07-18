package com.lucassilvs.springbootkeycloak.config.providers.crud;

import com.lucassilvs.springbootkeycloak.config.providers.crud.crud.CrudHttpClient;
import com.lucassilvs.springbootkeycloak.config.providers.crud.spi.CrudUserStorageProvider;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


import static com.lucassilvs.springbootkeycloak.config.providers.crud.spi.CrudUserStorageProviderConstants.*;

public class CrudUserStorageProviderFactory implements UserStorageProviderFactory<CrudUserStorageProvider> {

    protected final List<ProviderConfigProperty> configMetadata;
    private static final Logger log = LoggerFactory.getLogger(CrudUserStorageProviderFactory.class);

    public CrudUserStorageProviderFactory() {
        configMetadata = ProviderConfigurationBuilder.create()
                .property()
                .name(CRUD_BASE_URL.getValue())
                .label("URL base do CRUD")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("http://localhost:5000")
                .helpText("url base do servidor de identidade do CRUD")
                .add()
                .property()
                .name(CRUD_CREATE_USER_PATH.getValue())
                .label("Path de criação de usuário")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("usuario")
                .helpText("Path para criação de usuário no servidor de identidade do CRUD")
                .add()
                .property()
                .name(CRUD_GET_USER_PATH.getValue())
                .label("Path para busca de usuário")
                .defaultValue("usuario")
                .type(ProviderConfigProperty.STRING_TYPE)
                .helpText("Path para busca de dados de usuario no servidor de identidade do CRUD")
                .add()
                .property()
                .name(CRUD_UPDATE_USER_PATH.getValue())
                .label("Path para atualização de usuário")
                .defaultValue("usuario/atualizar-usuario")
                .type(ProviderConfigProperty.STRING_TYPE)
                .helpText("Path para atualização de dados de usuario no servidor de identidade do CRUD")
                .secret(true)
                .add()
                .property()
                .name(CRUD_VALIDATE_USER_PATH.getValue())
                .label("Path para validação de usuário")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("usuario/validar-credencial")
                .helpText("Path para validação de credenciais de usuario no servidor de identidade do CRUD")
                .secret(true)
                .add()
                .property()
                .name(CRUD_HEALTH_CHECK_URL.getValue())
                .label("URL de health check")
                .type(ProviderConfigProperty.STRING_TYPE)
                .helpText("Url de health check do servidor de identidade do CRUD")
                .defaultValue("http://localhost:5000/q/health/ready")
                .add()
                .build();
    }

    @Override
    public String getId() {
        return "crud-user-provider";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configMetadata;
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) throws ComponentValidationException {

        try {
            log.info("[I84] Testing connection..." );
            CrudHttpClient.healthCheck(config.get(CRUD_HEALTH_CHECK_URL.getValue()));
            log.info("[I92] Connection OK !" );
        }
        catch(Exception ex) {
            log.warn(ex.toString());
            log.warn("[W94] Unable to validate CRUD Server: ex={}", ex.getMessage());
            throw new ComponentValidationException("Unable to validate database connection",ex);
        }
    }

    @Override
    public void onUpdate(KeycloakSession session, RealmModel realm, ComponentModel oldModel, ComponentModel newModel) {
        log.info("[I94] onUpdate()" );
    }

    @Override
    public void onCreate(KeycloakSession session, RealmModel realm, ComponentModel model) {
        log.info("[I99] onCreate()" );
    }


    @Override
    public CrudUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        return new CrudUserStorageProvider(session,model);
    }
}

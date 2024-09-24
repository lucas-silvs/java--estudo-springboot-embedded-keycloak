package com.lucassilvs.springbootkeycloak.config.providers.realmprovider.custom.models;

import java.util.List;
import java.util.Map;

public class MigrationCredentialRequest {
    private String clientId;
    private String clientSecret;
    private Map<String, Object> attributes;
    private List<String> scopes;

    public String getClientId() {
        return clientId;
    }

    public MigrationCredentialRequest setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public MigrationCredentialRequest setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public MigrationCredentialRequest setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public MigrationCredentialRequest setScopes(List<String> scopes) {
        this.scopes = scopes;
        return this;
    }
}

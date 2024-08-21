package com.lucassilvs.springbootkeycloak.config.providers.realmprovider.custom.models;

import java.util.Map;

public class MigrationCredentialRequest {
    private String username;
    private Map<String,String> attributes;
    private String salt;
    private String hash;
    private String algorithm;
    private String iterations;

    public MigrationCredentialRequest(String username, Map<String, String> attributes, String salt, String hash, String algorithm, String iterations) {
        this.username = username;
        this.attributes = attributes;
        this.salt = salt;
        this.hash = hash;
        this.algorithm = algorithm;
        this.iterations = iterations;
    }

    public MigrationCredentialRequest() {
    }

    public String getUsername() {
        return username;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public MigrationCredentialRequest setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }

    public MigrationCredentialRequest setUsername(String username) {
        this.username = username;
        return this;
    }


    public String getSalt() {
        return salt;
    }

    public MigrationCredentialRequest setSalt(String salt) {
        this.salt = salt;
        return this;
    }

    public String getHash() {
        return hash;
    }

    public MigrationCredentialRequest setHash(String hash) {
        this.hash = hash;
        return this;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public MigrationCredentialRequest setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    public String getIterations() {
        return iterations;
    }

    public MigrationCredentialRequest setIterations(String iterations) {
        this.iterations = iterations;
        return this;
    }
}

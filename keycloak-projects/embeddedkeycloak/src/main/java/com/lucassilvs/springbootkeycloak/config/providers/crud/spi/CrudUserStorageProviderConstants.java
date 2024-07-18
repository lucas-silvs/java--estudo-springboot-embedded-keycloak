package com.lucassilvs.springbootkeycloak.config.providers.crud.spi;

public enum CrudUserStorageProviderConstants {
    CRUD_BASE_URL("url"),
    CRUD_CREATE_USER_PATH("createUserPath"),
    CRUD_VALIDATE_USER_PATH("validateUserPath"),
    CRUD_GET_USER_PATH("getUserPath"),
    CRUD_UPDATE_USER_PATH("updateUserPath"),
    CRUD_HEALTH_CHECK_URL("crudHealthCheckUrl");

    private final String value;

    CrudUserStorageProviderConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
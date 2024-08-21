package com.lucassilvs.springbootkeycloak.config.providers.realmprovider.custom;

public enum ProviderEnum {

    ROLE_ADMIN("admin"),

    ERROR_MESSAGE_BEARER_AUTH_REQUIRED("Necessario autenticacao com Bearer Token"),

    ERROR_MESSAGE_ROLE_REQUIRED("Credencial não pode acessar o recurso solicitado devido a control de acesso");

    private final String value;


    ProviderEnum(String value) {
        this.value = value;
    }

    public String getvalue(){
        return this.value;
    }





}

package com.lucassilvs.springbootkeycloak.config.providers.crud.crud;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CrudUserModel(

        @JsonProperty("id")
        String id,
        @JsonProperty("login")
        String login,
        @JsonProperty("nome")
        String nome,
        @JsonProperty("email")
        String email,
        @JsonProperty("cpf")
        String cpf,
        @JsonProperty("dataNascimento")
        String dataNascimento,
        @JsonProperty("telefone")
        String telefone,
        @JsonProperty("senha")
        String senha) {

    public CrudUserModel(String cpf, String senha) {
        this("","", "", "", cpf, "", "", senha);
    }

    public CrudUserModel(String id,String login, String nome, String email, String cpf, String dataNascimento, String telefone, String senha) {
        this.id = id;
        this.login = login;
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.senha = senha;
    }
}

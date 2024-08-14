package com.lucassilvs.springbootkeycloak.config.providers.realmprovider.custom.models;

public class CustomUserRequest {

    private String username;
    private String email;

    private  String nome;
    private  String cpf;
    private  String dataNascimento;
    private  String telefone;
    private  String senha;

    public CustomUserRequest() {
    }

    public CustomUserRequest(String username, String email, String nome, String cpf, String dataNascimento, String telefone, String senha) {
        this.username = username;
        this.email = email;
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.senha = senha;
    }

    public String getUsername() {
        return username;
    }

    public CustomUserRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public CustomUserRequest setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getNome() {
        return nome;
    }

    public CustomUserRequest setNome(String nome) {
        this.nome = nome;
        return this;
    }

    public String getCpf() {
        return cpf;
    }

    public CustomUserRequest setCpf(String cpf) {
        this.cpf = cpf;
        return this;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public CustomUserRequest setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
        return this;
    }

    public String getTelefone() {
        return telefone;
    }

    public CustomUserRequest setTelefone(String telefone) {
        this.telefone = telefone;
        return this;
    }

    public String getSenha() {
        return senha;
    }

    public CustomUserRequest setSenha(String senha) {
        this.senha = senha;
        return this;
    }

    // Outros campos customizados

    // Getters e Setters


}

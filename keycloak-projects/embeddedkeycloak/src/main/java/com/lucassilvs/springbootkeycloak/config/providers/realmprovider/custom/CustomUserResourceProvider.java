package com.lucassilvs.springbootkeycloak.config.providers.realmprovider.custom;

import com.lucassilvs.springbootkeycloak.config.providers.realmprovider.custom.models.CustomUserRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.resource.RealmResourceProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomUserResourceProvider implements RealmResourceProvider {

    private final KeycloakSession keycloakSession;

    //Campo para autenticação com Token JWT
    private final AuthenticationManager.AuthResult auth;

    public CustomUserResourceProvider(KeycloakSession keycloakSession) {
        this.keycloakSession = keycloakSession;
        this.auth = new AppAuthManager.BearerTokenAuthenticator(keycloakSession).authenticate();
    }


    @Override
    public Object getResource() {
        return this;
    }

    @Override
    public void close() {
        // Não implementando devido a não customização do close
    }

    @Path("create-custom-user")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCustomUser( CustomUserRequest customUserRequest) {
        // validação se está utilizando autenticação com Bearer Token
        if (this.auth == null || this.auth.getToken() == null){
            throw new NotAuthorizedException("Necessario autenticacao com Bearer Token");
        }
        // Validação para identificar se o token informado possui a role "admin"
        if (!this.auth.getToken().getRealmAccess().isUserInRole("admin")){
            throw new ForbiddenException("Credencial não pode acessar o recurso solicitado devido a control de acesso");
        }

        // Adicione outras propriedades necessárias
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("telefone", Collections.singletonList(customUserRequest.getTelefone()));
        attributes.put("dataNascimento", Collections.singletonList(customUserRequest.getDataNascimento()));
        attributes.put("CPF", Collections.singletonList(customUserRequest.getCpf()));
        attributes.put("nomeCompleto", Collections.singletonList(customUserRequest.getNome()));

        String[] nameSplit = customUserRequest.getNome().split(" ");
        String name = nameSplit[0];
        String surname = nameSplit[nameSplit.length - 1];

        // Criação do usuário via API interna do Keycloak
        RealmModel realm = keycloakSession.getContext().getRealm();
        UserModel user = keycloakSession.users().addUser(realm, customUserRequest.getUsername());
        user.setEmail(customUserRequest.getEmail());
        user.setFirstName(name);
        user.setLastName(surname);
        user.setEmailVerified(true);
        attributes.forEach(user::setAttribute);
        user.setEnabled(true);

        user.credentialManager().updateCredential(UserCredentialModel.password(customUserRequest.getSenha()));

        return Response.status(Response.Status.CREATED).build();
    }
}

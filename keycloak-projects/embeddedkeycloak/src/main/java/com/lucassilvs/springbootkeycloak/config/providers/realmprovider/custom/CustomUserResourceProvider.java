package com.lucassilvs.springbootkeycloak.config.providers.realmprovider.custom;

import com.lucassilvs.springbootkeycloak.config.providers.realmprovider.custom.models.CustomUserRequest;
import com.lucassilvs.springbootkeycloak.config.providers.realmprovider.custom.models.MigrationCredentialRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.keycloak.models.*;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.resource.RealmResourceProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lucassilvs.springbootkeycloak.config.providers.realmprovider.custom.ProviderEnum.ERROR_MESSAGE_BEARER_AUTH_REQUIRED;
import static com.lucassilvs.springbootkeycloak.config.providers.realmprovider.custom.ProviderEnum.ERROR_MESSAGE_ROLE_REQUIRED;

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
        validateToken();

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

    @Path("migrate-client")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUserFromExternalProvider( MigrationCredentialRequest migrationCredentialRequest) {
        validateToken();

        RealmModel realm = keycloakSession.getContext().getRealm();

        ClientModel clientModel = keycloakSession.clients().addClient(realm, migrationCredentialRequest.getUsername());

        migrationCredentialRequest.getAttributes().forEach(clientModel::setAttribute);

        clientModel.setServiceAccountsEnabled(true);
        clientModel.setProtocol("openid-connect");
        clientModel.setClientAuthenticatorType("client-secret");
        clientModel.setStandardFlowEnabled(true);
        clientModel.setDirectAccessGrantsEnabled(true);
        clientModel.setSecret(migrationCredentialRequest.getHash());


        return Response.status(Response.Status.CREATED).build();
    }

    @Path("migrate-client")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientFromExternalProvider( @QueryParam("clientId") String clientId) {
        // validação se está utilizando autenticação com Bearer Token
        validateToken();


        RealmModel realm = keycloakSession.getContext().getRealm();

        ClientModel clientModel = keycloakSession.clients().getClientByClientId(realm, clientId);

        return Response.ok(clientModel.toString()).build();
    }

    @Path("migrate-client")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteClientFromExternalProvider( @QueryParam("clientId") String clientId) {
        // validação se está utilizando autenticação com Bearer Token
        validateToken();


        RealmModel realm = keycloakSession.getContext().getRealm();

        ClientModel clientModel = keycloakSession.clients().getClientByClientId(realm, clientId);

        keycloakSession.clients().removeClient(realm, clientModel.getId());

        return Response.noContent().build();
    }

    private void validateToken() {
        // validação para identificar se está sendo informando um token JWT
        if (this.auth == null || this.auth.getToken() == null) {
            throw new NotAuthorizedException(ERROR_MESSAGE_BEARER_AUTH_REQUIRED.getvalue());
        }
        // Validação para identificar se o token informado possui a role "admin"
        if (!this.auth.getToken().getRealmAccess().isUserInRole(ProviderEnum.ROLE_ADMIN.getvalue())) {
            throw new ForbiddenException(ERROR_MESSAGE_ROLE_REQUIRED.getvalue());
        }
    }
}

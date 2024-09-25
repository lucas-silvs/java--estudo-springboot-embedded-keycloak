package com.lucassilvs.springbootkeycloak.config.providers.realmprovider.custom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucassilvs.springbootkeycloak.config.providers.realmprovider.custom.models.CustomUserRequest;
import com.lucassilvs.springbootkeycloak.config.providers.realmprovider.custom.models.MigrationCredentialRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.mappers.AudienceProtocolMapper;
import org.keycloak.protocol.oidc.mappers.HardcodedClaim;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.resource.RealmResourceProvider;

import java.util.*;
import java.util.stream.Collectors;

import static com.lucassilvs.springbootkeycloak.config.providers.realmprovider.custom.ProviderEnum.ERROR_MESSAGE_BEARER_AUTH_REQUIRED;
import static com.lucassilvs.springbootkeycloak.config.providers.realmprovider.custom.ProviderEnum.ERROR_MESSAGE_ROLE_REQUIRED;

public class CustomUserResourceProvider implements RealmResourceProvider {

    public static final String JSON_TYPE_LABEL = "jsonType.label";
    public static final String TYPE_LABEL_JSON = "JSON";
    public static final String TYPE_LABE_STRING = "String";
    public static final String CLAIM_VALUE = "claim.value";
    public static final String PROTOCOL_OPENID_CONNECT = "openid-connect";
    private final KeycloakSession keycloakSession;

    private final ObjectMapper objectMapper;

    //Campo para autenticação com Token JWT
    private final AuthenticationManager.AuthResult auth;

    public CustomUserResourceProvider(KeycloakSession keycloakSession) {
        this.keycloakSession = keycloakSession;
        this.auth = new AppAuthManager.BearerTokenAuthenticator(keycloakSession).authenticate();
        this.objectMapper = new ObjectMapper();
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
    public Response createCustomUser(CustomUserRequest customUserRequest) {
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
    public Response createUserFromExternalProvider(MigrationCredentialRequest migrationCredentialRequest) {

        validateToken();

        RealmModel realm = keycloakSession.getContext().getRealm();

        ClientModel clientModel = keycloakSession.clients().addClient(realm, migrationCredentialRequest.getClientId());

        Set<ClientScopeModel> scopeModels = migrationCredentialRequest.getScopes()
                .stream()
                .map(scope -> createOrReturnExistentScope(scope, realm)).collect(Collectors.toSet());
        clientModel.addClientScopes(scopeModels, false);

        createAudienceFieldClient(clientModel, migrationCredentialRequest.getClientId());

        // Configurar as propriedades do mapeamento

        migrationCredentialRequest.getAttributes().forEach((key, value) -> {
            ProtocolMapperModel mapper = mappingFieldToCustomMapper(key, value);
            clientModel.addProtocolMapper(mapper);
        });

        createClient(migrationCredentialRequest, clientModel);

        return Response.status(Response.Status.CREATED).build();
    }

    private void createAudienceFieldClient(ClientModel clientModel, String clientId) {
        Map<String, String> configMapper = new HashMap<>();
        configMapper.put("access.token.claim", "true");
        configMapper.put("userinfo.token.claim", "true");
        configMapper.put("included.custom.audience", clientId);

        ProtocolMapperModel mapper = new ProtocolMapperModel();
        mapper.setName(String.format("audience-%s-mapper", clientId));
        mapper.setProtocol(PROTOCOL_OPENID_CONNECT);
        mapper.setProtocolMapper(AudienceProtocolMapper.PROVIDER_ID);
        mapper.setConfig(configMapper);

        clientModel.addProtocolMapper(mapper);
    }

    private ProtocolMapperModel mappingFieldToCustomMapper(String key, Object value) {
        Map<String, String> configMapper = new HashMap<>();
        configMapper.put("access.token.claim", "true");
        configMapper.put("userinfo.token.claim", "true");
        configMapper.put("claim.name", key);

        // Identifica se o valor é uma string ou um objeto JSON
        try {
            if (value instanceof String stringValue) {
                JsonNode jsonNode = objectMapper.readTree(stringValue);
                if (jsonNode.isObject()) {
                    configMapper.put(CLAIM_VALUE, jsonNode.toString());
                    configMapper.put(JSON_TYPE_LABEL, TYPE_LABEL_JSON);
                } else if (jsonNode.isTextual()) {
                    configMapper.put(CLAIM_VALUE, jsonNode.asText());
                    configMapper.put(JSON_TYPE_LABEL, TYPE_LABE_STRING);
                }
            } else if (value instanceof Map) {
                // Se o valor já for um mapa, converte para JSON
                JsonNode jsonNode = objectMapper.valueToTree(value);
                configMapper.put(CLAIM_VALUE, jsonNode.toString());
                configMapper.put(JSON_TYPE_LABEL, TYPE_LABEL_JSON);
            } else {
                // Trate outros tipos conforme necessário
                configMapper.put(CLAIM_VALUE, value.toString());
                configMapper.put(JSON_TYPE_LABEL, TYPE_LABE_STRING);
            }
        } catch (Exception e) {
            // Se ocorrer um erro durante a desserialização, trate como string
            configMapper.put(CLAIM_VALUE, value.toString());
            configMapper.put(JSON_TYPE_LABEL, TYPE_LABE_STRING);
        }

        ProtocolMapperModel mapper = new ProtocolMapperModel();
        mapper.setName(String.format("custom-field-%s-mapper", key));
        mapper.setProtocol(PROTOCOL_OPENID_CONNECT);
        mapper.setProtocolMapper(HardcodedClaim.PROVIDER_ID);
        mapper.setConfig(configMapper);
        return mapper;
    }

    private ClientScopeModel createOrReturnExistentScope(String scope, RealmModel realm) {

        ClientScopeModel scopeModel = keycloakSession.clientScopes()
                .getClientScopesStream(realm)
                .filter(clientScope -> scope.equals(clientScope.getName()))
                .findFirst()
                .orElse(null);

        if (scopeModel == null) {

            scopeModel = keycloakSession.clientScopes().addClientScope(realm, scope);
            scopeModel.setDescription(scope);
            scopeModel.setIncludeInTokenScope(true);
            scopeModel.setProtocol(PROTOCOL_OPENID_CONNECT);
        }

        return scopeModel;
    }

    private static void createClient(MigrationCredentialRequest migrationCredentialRequest, ClientModel clientModel) {
        clientModel.setServiceAccountsEnabled(true);
        clientModel.setProtocol(PROTOCOL_OPENID_CONNECT);
        clientModel.setClientAuthenticatorType("client-secret");
        clientModel.setStandardFlowEnabled(true);
        clientModel.setDirectAccessGrantsEnabled(true);
        clientModel.setProtocol(PROTOCOL_OPENID_CONNECT);
        clientModel.setStandardFlowEnabled(false);
        clientModel.setSecret(migrationCredentialRequest.getClientSecret());
    }

    @Path("migrate-client")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientFromExternalProvider(@QueryParam("clientId") String clientId) {
        // validação se está utilizando autenticação com Bearer Token
        validateToken();


        RealmModel realm = keycloakSession.getContext().getRealm();

        ClientModel clientModel = keycloakSession.clients().getClientByClientId(realm, clientId);

        return Response.ok(clientModel.toString()).build();
    }

    @Path("migrate-client")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteClientFromExternalProvider(@QueryParam("clientId") String clientId) {
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

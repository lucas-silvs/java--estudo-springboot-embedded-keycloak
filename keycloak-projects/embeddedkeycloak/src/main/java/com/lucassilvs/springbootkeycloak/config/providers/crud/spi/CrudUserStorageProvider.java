package com.lucassilvs.springbootkeycloak.config.providers.crud.spi;

import com.lucassilvs.springbootkeycloak.config.providers.crud.crud.CrudHttpClient;
import com.lucassilvs.springbootkeycloak.config.providers.crud.crud.CrudUserModel;
import com.lucassilvs.springbootkeycloak.config.providers.custom.db.DbUtil;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.lucassilvs.springbootkeycloak.config.providers.crud.spi.CrudUserStorageProviderConstants.*;

public class CrudUserStorageProvider implements
        UserStorageProvider, // Essa interface é responsável por prover a implementação de armazenamento de usuários
        UserLookupProvider, // Essa interface é responsável por prover a implementação de busca de usuários
        CredentialInputValidator, // Essa interface é responsável por prover a implementação de validação de credenciais
        UserQueryProvider // Essa interface é responsável por prover a implementação de busca de usuários
{

    private final KeycloakSession ksession;
    private final ComponentModel model;
    private static final Logger log = LoggerFactory.getLogger(CrudUserStorageProvider.class);


    public CrudUserStorageProvider(KeycloakSession ksession, ComponentModel model) {
        this.ksession = ksession;
        this.model = model;
    }

    public void close() {
        log.info("[I30] close()");
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        log.info("[I35] getUserById({})", id);
        StorageId sid = new StorageId(id);
        return getUserByUsername(realm, sid.getExternalId());
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        log.info("[I41] getUserByUsername({})", username);

        CrudUserModel crudUser = CrudHttpClient.getUserByUsername(model.get(CRUD_BASE_URL.getValue()), model.get(CRUD_GET_USER_PATH.getValue()), username);
        if (crudUser != null) {
            return mapUser(realm, crudUser);
        }
        return null;
    }

    @Override
    public UserModel getUserByEmail(RealmModel realmModel, String s) {
        log.info("[I41] getUserByEmail({})", s);

        return getUserByUsername(realmModel, s);
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        log.info("[I57] supportsCredentialType({})", credentialType);
        return PasswordCredentialModel.TYPE.endsWith(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        log.info("[I57] isConfiguredFor(realm={},user={},credentialType={})", realm.getName(), user.getUsername(), credentialType);
        // In our case, password is the only type of credential, so we allways return 'true' if
        // this is the credentialType
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        log.info("[I57] isValid(realm={},user={},credentialInput.type={})", realm.getName(), user.getUsername(), credentialInput.getType());
        if (!this.supportsCredentialType(credentialInput.getType())) {
            return false;
        }
        StorageId sid = new StorageId(user.getId());
        String username = sid.getExternalId();


        return CrudHttpClient.validateUserCredentials(model.get(CRUD_BASE_URL.getValue()), model.get(CRUD_VALIDATE_USER_PATH.getValue()), new CrudUserModel(
                username,
                credentialInput.getChallengeResponse()
        ));
    }


// UserQueryProvider implementation

    @Override
    public int getUsersCount(RealmModel realm) {
        log.info("[I113] getUsersCount: realm={}", realm.getName());

        return CrudHttpClient.listUsers(model.get(CRUD_BASE_URL.getValue()), model.get(CRUD_GET_USER_PATH.getValue())).size();
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group, Integer firstResult, Integer maxResults) {
        log.info("[I113] getUsers: realm={}", realm.getName());

        List<CrudUserModel> crudUserModels = CrudHttpClient.listUsers(model.get(CRUD_BASE_URL.getValue()), model.get(CRUD_GET_USER_PATH.getValue()));
        return crudUserModels.stream().map(crudUserModel -> mapUser(realm, crudUserModel));

    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, String search, Integer firstResult, Integer maxResults) {
        log.info("[I139] searchForUser: realm={}", realm.getName());

        List<CrudUserModel> crudUserModels = CrudHttpClient.listUsers(model.get(CRUD_BASE_URL.getValue()), model.get(CRUD_GET_USER_PATH.getValue()));
        return crudUserModels.stream().map(crudUserModel -> mapUser(realm, crudUserModel));

    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult, Integer maxResults) {
        return getGroupMembersStream(realm, null, firstResult, maxResults);
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realm, String attrName, String attrValue) {
        return Stream.empty();
    }

    private UserModel mapUser(RealmModel realm, CrudUserModel crudUser) {
        String[] nameSplit = crudUser.nome().split(" ");
        return new CrudUser.Builder(ksession, realm, model, crudUser.cpf())
                .email(crudUser.email())
                .firstName(nameSplit[0])
                .lastName(nameSplit[nameSplit.length - 1])
                .birthDate(crudUser.dataNascimento())
                .phone(crudUser.telefone())
                .build();
    }
}

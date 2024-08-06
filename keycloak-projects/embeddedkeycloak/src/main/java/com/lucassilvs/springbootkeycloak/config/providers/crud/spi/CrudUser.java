package com.lucassilvs.springbootkeycloak.config.providers.crud.spi;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.LegacyUserCredentialManager;
import org.keycloak.models.*;
import org.keycloak.storage.adapter.AbstractUserAdapter;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

class CrudUser extends
        AbstractUserAdapter // Essa classe é responsável por prover a implementação de armazenamento de usuários
 {

    private final String username;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String birthDate;
    private final String phone;
     private Set<RoleModel> roles;


     private CrudUser(KeycloakSession session, RealmModel realm,
                     ComponentModel storageProviderModel,
                     String username,
                     String email,
                     String firstName,
                     String lastName,
                     String birthDate, String phone) {
        super(session, realm, storageProviderModel);
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.roles = Set.of(realm.getDefaultRole());
    }




    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getPhone() {
        return phone;
    }

     public void setRoles(Set<RoleModel> roles) {
         this.roles = roles;
     }

     @Override
     public Set<RoleModel> getRealmRoleMappings() {
         return roles;
     }

     @Override
     public void grantRole(RoleModel role) {
         roles.add(role);
     }

     @Override
     public void deleteRoleMapping(RoleModel role) {
         roles.remove(role);
     }

     @Override
     public Set<RoleModel> getRoleMappings() {
         return roles;
     }

    @Override
    public Map<String, List<String>> getAttributes() {
        MultivaluedHashMap<String, String> attributes = new MultivaluedHashMap<>();
        attributes.add(UserModel.USERNAME, getUsername());
        attributes.add(UserModel.EMAIL, getEmail());
        attributes.add(UserModel.FIRST_NAME, getFirstName());
        attributes.add(UserModel.LAST_NAME, getLastName());
        attributes.add("birthDate", getBirthDate().toString());
        attributes.add("phone", getPhone());
        return attributes;
    }

    static class Builder {
        private final KeycloakSession session;
        private final RealmModel realm;
        private final ComponentModel storageProviderModel;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String birthDate;
        private String phone;

        Builder(KeycloakSession session, RealmModel realm, ComponentModel storageProviderModel, String username) {
            this.session = session;
            this.realm = realm;
            this.storageProviderModel = storageProviderModel;
            this.username = username;
        }

        Builder email(String email) {
            this.email = email;
            return this;
        }

        Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        Builder birthDate(String birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        CrudUser build() {
            return new CrudUser(
                    session,
                    realm,
                    storageProviderModel,
                    username,
                    email,
                    firstName,
                    lastName,
                    birthDate,
                    phone);

        }
    }

    @Override
    public SubjectCredentialManager credentialManager() {
        return new LegacyUserCredentialManager(session, realm, this);
    }
}
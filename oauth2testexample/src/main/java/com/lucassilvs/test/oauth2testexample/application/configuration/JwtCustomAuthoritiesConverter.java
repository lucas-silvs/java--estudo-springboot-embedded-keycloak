package com.lucassilvs.test.oauth2testexample.application.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Converter personalizado para extrair authorities do JWT.
 * Extrai tanto scopes OAuth2 padrão quanto roles do Keycloak.
 */
@Component
public class JwtCustomAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final JwtGrantedAuthoritiesConverter scopesConverter;

    public JwtCustomAuthoritiesConverter() {
        this.scopesConverter = new JwtGrantedAuthoritiesConverter();
        this.scopesConverter.setAuthorityPrefix("SCOPE_");
        this.scopesConverter.setAuthoritiesClaimName("scope");
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // Converte scopes padrão OAuth2
        Collection<GrantedAuthority> scopes = scopesConverter.convert(jwt);
        authorities.addAll(scopes);

        // Extrai roles do Keycloak
        authorities.addAll(extractRealmRoles(jwt));
        authorities.addAll(extractResourceRoles(jwt));

        return authorities;
    }

    /**
     * Extrai roles do realm do Keycloak do claim 'realm_access.roles'
     */
    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null) {
            return Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) realmAccess.get("roles");
        if (roles == null) {
            return Collections.emptyList();
        }

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Extrai roles específicos de recursos do claim 'resource_access'
     */
    private Collection<GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess == null) {
            return Collections.emptyList();
        }

        return resourceAccess.values().stream()
                .filter(Map.class::isInstance)
                .map(resource -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> clientAccess = (Map<String, Object>) resource;
                    return clientAccess;
                })
                .map(clientAccess -> {
                    @SuppressWarnings("unchecked")
                    List<String> roles = (List<String>) clientAccess.get("roles");
                    return roles;
                })
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}

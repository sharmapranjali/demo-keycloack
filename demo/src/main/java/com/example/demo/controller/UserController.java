package com.example.demo.controller;


import com.example.demo.model.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.common.util.Base64Url;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/users")
@Slf4j
public class UserController {


    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String SSOLogin(HttpServletRequest httpServletRequest) {
        System.out.println("Hello");
        Keycloak keycloak = KeycloakBuilder.builder().serverUrl(authServerUrl)
                .grantType(OAuth2Constants.PASSWORD).realm("master").clientId("admin-cli")
                .username("admin").password("admin")
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();

        keycloak.tokenManager().getAccessToken();
        String redirectUri = "http://localhost:8081/test/redirect";
        String nonce = UUID.randomUUID().toString();
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        String input = nonce + clientId ;
        byte[] check = md.digest(input.getBytes(StandardCharsets.UTF_8));
        String hash = Base64Url.encode(check);
        httpServletRequest.getSession().setAttribute("hash", hash);

        return  KeycloakUriBuilder.fromUri(authServerUrl)
                .path("/realms/demo/protocol/openid-connect/auth") // Url changed
                .queryParam("response_type", "code") // Autherization Code Flow
                .queryParam("scope", "openid") // Add additional scopes if needed
                .queryParam("nonce", nonce)
                .queryParam("client_id", clientId)
                .queryParam("response_mode","fragment")
                .queryParam("code_challenge",hash)
                .queryParam("code_challenge_method","S256")
                .queryParam("redirect_uri", redirectUri).build(realm).toString();



    }
}

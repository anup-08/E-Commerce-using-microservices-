package com.authServer.service;

import com.example.dtoRequest.UserRequest;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    @Value("${keycloak.server-url}")
    private String serverUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.client-id}")
    private String clientId;
    @Value("${keycloak.client-secret}")
    private String clientSecret;
    @Value("${keycloak.admin-username}")
    private String adminUserName;
    @Value("${keycloak.admin-password}")
    private String adminPassword;

    //1.Get a keycloak client logged in as an admin
    private Keycloak getAdminKeyCloak(){
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master")
                .grantType(OAuth2Constants.PASSWORD)
                .clientId("admin-cli")
                .username(adminUserName)
                .password(adminPassword)
                .build();
    }

    //2.Signup new user and assign role
    public String registerInKeycloak(UserRequest request) {
        Keycloak keycloak = getAdminKeyCloak();

        // To create new user
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstname());
        user.setLastName(request.getLastname());
        user.setEmail(request.getEmail());
        user.setEnabled(true);

        Map<String , List<String>> attributesMap = new HashMap<>();
        attributesMap.put("gender" , List.of(request.getGender().trim().toLowerCase(Locale.ROOT)));

        user.setAttributes(attributesMap);

        //TO Set password
        CredentialRepresentation password = new CredentialRepresentation();
        password.setTemporary(false);
        password.setType(CredentialRepresentation.PASSWORD);
        password.setValue(request.getPassword());

        user.setCredentials(Collections.singletonList(password));

        // Send Response To keyClock
        Response response = keycloak.realm(realm).users().create(user);
        if(response.getStatus() != 201){
            throw new IllegalArgumentException("Failed to create user : " + response.getStatusInfo());
        }

        //Get newly userId
        String userId = CreatedResponseUtil.getCreatedId(response);

        //Assign role
        String role;
        if(request.getRole() == null ){
            role = "USER";
        }else{
            role = request.getRole().toUpperCase();
        }

        RoleRepresentation setRole = keycloak.realm(realm).roles()
                .get(role)
                .toRepresentation();
        keycloak.realm(realm).users().get(userId).roles().realmLevel().add(Collections.singletonList(setRole));

        return userId;
    }

    public UserRepresentation findUserById(String id){
        Keycloak keycloak = getAdminKeyCloak();
        return keycloak.realm(realm).users().get(id).toRepresentation();
    }

    public List<String> getUserRole(String userId){
        Keycloak keycloak = getAdminKeyCloak();

        List<RoleRepresentation> representations = keycloak.realm(realm).users()
                .get(userId).roles().realmLevel().listEffective();

        return representations.stream().map(RoleRepresentation::getName) // role -> role.getName()
                .collect(Collectors.toList());
    }

    public void deleteUser(String id){
        Keycloak keycloak = getAdminKeyCloak();
        keycloak.realm(realm).users().get(id).remove();
    }
}

package com.authServer.service;

import com.example.dtoRequest.UserRequest;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class KeycloakService {


    @Value("${keycloak.realm}")
    private String realm;

    //1.Get a keycloak client logged in as an admin
    private final Keycloak getAdminKeycloak;


    //2.Signup new user and assign role
    public UserRepresentation registerInKeycloak(UserRequest request) {

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
        Response response = getAdminKeycloak.realm(realm).users().create(user);
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

        try{
            RoleRepresentation setRole = getAdminKeycloak.realm(realm).roles()
                    .get(role)
                    .toRepresentation();
            getAdminKeycloak.realm(realm).users().get(userId).roles().realmLevel().add(Collections.singletonList(setRole));
        } catch (Exception e) {
            deleteUser(userId);
            throw new IllegalArgumentException("unable to assign the role "+e);
        }

        return findUserById(userId);
    }

    public UserRepresentation findUserById(String id){
        return getAdminKeycloak.realm(realm).users().get(id).toRepresentation();
    }

//    public List<String> getUserRole(String userId){
//        List<RoleRepresentation> representations = getAdminKeycloak.realm(realm).users()
//                .get(userId).roles().realmLevel().listEffective();
//
//        return representations.stream().map(RoleRepresentation::getName) // role -> role.getName()
//                .collect(Collectors.toList());
//    }

    public void deleteUser(String id){
        getAdminKeycloak.realm(realm).users().get(id).remove();
    }
}

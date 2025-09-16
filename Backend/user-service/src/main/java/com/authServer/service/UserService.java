package com.authServer.service;

import com.authServer.entity.Address;
import com.authServer.entity.Contact;
import com.authServer.entity.User;
import com.authServer.repo.UserRepo;
import com.example.dtoRequest.AddressDto;
import com.example.dtoRequest.ContactDto;
import com.example.dtoRequest.UserRequest;
import com.example.dtoResponse.UserResponse;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo repo;
    private  final KeycloakService keycloakService;

    public UserResponse registerUser(UserRequest request) {
        String keycloakId = null;
        try{
            keycloakId = keycloakService.registerInKeycloak(request);

            UserRepresentation keycloakUser = keycloakService.findUserById(keycloakId);
            List<String> getRole = keycloakService.getUserRole(keycloakId);

            User user = User.builder()
                    .keyCloakId(keycloakId)
                    .build();


            if(request.getContact() != null){
                Contact contact = Contact.builder()
                        .phoneNumber(request.getContact().getPhoneNumber())
                        .alternatePhone(request.getContact().getAlternatePhone())
                        .secondaryEmail(request.getContact().getSecondaryEmail())
                        .user(user)
                        .build();
                user.setContact(contact);
            }

            if (request.getAddresses() != null) {
                List<Address> addresses = request.getAddresses()
                        .stream().map(addressDto -> Address.builder()
                                .area(addressDto.getArea())
                                .state(addressDto.getState())
                                .postalCode(addressDto.getPostalCode())
                                .city(addressDto.getCity())
                                .country(addressDto.getCountry())
                                .user(user)
                                .build()
                        ).toList();
                user.setAddresses(addresses);
            }

            return convertToResponse(repo.save(user) , keycloakUser , getRole);

        } catch (Exception e) {
            if ( keycloakId != null) {
                // Rollback the Keycloak user creation
                keycloakService.deleteUser(keycloakId);
            }
            throw new RuntimeException("Failed to register user. The operation was rolled back.", e);
        }
    }

    private UserResponse convertToResponse(User user, UserRepresentation keycloakUser, List<String> roles) {
        List<AddressDto> addressDto = new ArrayList<>();
        if(user.getAddresses() != null && !user.getAddresses().isEmpty()){
            addressDto = user.getAddresses().stream()
                    .map(this::convertTOAddressDto) // address -> convertTOAddressDto(address)
                    .toList();
        }


        ContactDto contactDto = null;
        if(user.getContact() != null){
            contactDto = new ContactDto(user.getContact().getPhoneNumber(),user.getContact().getAlternatePhone()
                    ,user.getContact().getSecondaryEmail());
        }

        String gender = "";
        if(keycloakUser.getAttributes() != null && keycloakUser.getAttributes().containsKey("gender")){
            List<String> getAttributes = keycloakUser.getAttributes().get("gender");
            if(getAttributes != null && !getAttributes.isEmpty()){
                gender = getAttributes.getFirst(); // getAttributes.get(0);
            }
        }

        String role = roles.stream().findFirst().orElse(null);

        return UserResponse.builder()
                //data from db
                .id(user.getId())
                .createdAt(user.getCreatedAt())
                .isActive(user.getIsActive())
                .addresses(addressDto)
                .contact(contactDto)

                //data from keycloak
                .username(keycloakUser.getUsername())
                .firstName(keycloakUser.getFirstName())
                .lastName(keycloakUser.getLastName())
                .email(keycloakUser.getEmail())
                .gender(gender.toUpperCase())
                .role(role)
                .build();
    }


    private AddressDto convertTOAddressDto(Address address) {
        return AddressDto.builder()
                .area(address.getArea())
                .state(address.getState())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .build();
    }

}


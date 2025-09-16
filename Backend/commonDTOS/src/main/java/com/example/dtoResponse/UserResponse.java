package com.example.dtoResponse;

import com.example.dtoRequest.AddressDto;
import com.example.dtoRequest.ContactDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String role;
    private LocalDateTime createdAt;
    private boolean isActive;

    @Builder.Default
    private ContactDto contact = new ContactDto();

    @Builder.Default
    private List<AddressDto> addresses = new ArrayList<>();
}

package com.example.dtoRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank(message = "First name cannot be empty.")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters.")
    private String firstname;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters.")
    private String lastname;

    @NotBlank(message = "Username cannot be empty.")
    @Size(min = 3, max = 25, message = "Username must be between 3 and 25 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username doesn't contains any special characters .")
    private String username;

    @NotBlank(message = "Password cannot be empty.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character.")
    private String password;

    @NotBlank(message = "Gender cannot be empty.")
    @Pattern(regexp = "Male|Female|Other", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Invalid Gender")
    private String gender;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email address.")
    private String email;

    @Valid // This annotation is crucial to trigger validation on the nested object
    private ContactDto contact;

    @Valid // This ensures each address in the list is validated
    private List<AddressDto> addresses;

    private String role;
}

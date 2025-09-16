package com.example.dtoRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactDto {

    @Size(min = 10, max = 10)
    private String phoneNumber;

    @Size(min = 10, max = 10)
    private String alternatePhone;

    @Email(message = "Please provide a valid secondary email address.")
    private String secondaryEmail;
}

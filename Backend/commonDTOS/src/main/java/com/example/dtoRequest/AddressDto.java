package com.example.dtoRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDto {
    @NotBlank(message = "Area cannot be empty.")
    private String area;

    @NotBlank(message = "City cannot be empty.")
    private String city;

    @NotBlank(message = "State cannot be empty.")
    private String state;

    @NotBlank(message = "Postal code cannot be empty.")
    private String postalCode;

    @NotBlank(message = "Country cannot be empty.")
    private String country;
}

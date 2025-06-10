package com.ecommerce.orderservice.order_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateOrderRequest(

        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        String email,

        @Pattern(
                regexp = "^[\\p{L}]+(?:[\\s\\p{L}-]+)*$",
                message = "First name must contain only letters and spaces"
        )
        @NotBlank(message = "First name is required")
        String firstName,

        @Pattern(
                regexp = "^[\\p{L}]+(?:[\\s\\p{L}-]+)*$",
                message = "Last name must contain only letters and spaces"
        )
        @NotBlank(message = "Last name is required")
        String lastName,

        @Pattern(
                regexp = "^\\+?[0-9]{1,4}[\\s.-]?[0-9]{3,}[\\s.-]?[0-9]{3,}[\\s.-]?[0-9]{0,4}$",
                message = "Invalid phone number format"
        )
        @NotBlank(message = "Phone number is required")
        String phoneNumber,


        @Pattern(
                regexp = "^[\\p{L}0-9 .,'-]+$",
                message = "Invalid street format"
        )
        @NotBlank(message = "Street is required")
        String street,

        @Pattern(
                regexp = "^[\\p{L}0-9/\\-]+$",
                message = "Invalid house number format"
        )
        @NotBlank(message = "House number is required")
        String houseNumber,

        @NotBlank(message = "City is required")
        String city,

        @Pattern(
                regexp = "^[0-9A-Za-z\\-\\s]{3,10}$",
                message = "Invalid ZIP/postal code format"
        )
        @NotBlank(message = "Zip code is required")
        String zipCode,

        @Pattern(
                regexp = "^[\\p{L} .'-]+$",
                message = "Country must contain only letters"
        )
        @NotBlank(message = "Country is required")
        String country
) {
}

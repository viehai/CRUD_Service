package com.viehai.identity_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressRequest {
    @NotBlank @Size(max = 255) String line1;
    @Size(max = 255) String line2;
    @Size(max = 100) String ward;
    @Size(max = 100) String district;
    @NotBlank @Size(max = 100) String city;
    @Size(max = 100) String country = "VN";
    @Size(max = 20)  String postalCode;
}

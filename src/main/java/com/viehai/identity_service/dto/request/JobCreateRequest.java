package com.viehai.identity_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data @NoArgsConstructor @AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobCreateRequest {
    String code;
    String name;
}

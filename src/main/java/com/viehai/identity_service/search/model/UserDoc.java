package com.viehai.identity_service.search.model;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.Searchable;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDoc {
    @Id
    String id;

    @Indexed
    String username;      // lọc chính xác

    @Searchable
    String firstName;     // full-text

    @Searchable
    String lastName;      // full-text
}

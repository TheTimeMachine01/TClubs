package com.ashish.clubs.common.models.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userId;

    @NotBlank
    @Size(min = 3, max = 50)
    private String email;
    private String passwordHash; // Store hash, not plain password. Only for Auth service internal use.
    private String profilePictureUrl;
    private Set<Role> roles; // Enum or separate Role object
    private boolean enabled;
    private boolean accountLocked;
    private Instant createdAt;
    private Instant updatedAt;
}

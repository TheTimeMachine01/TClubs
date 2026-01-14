package com.ashish.clubs.common.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile  implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userId; // Foreign key to User
    private String firstName;
    private String lastName;
    private String bio;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    // Add address, social media links etc.
}

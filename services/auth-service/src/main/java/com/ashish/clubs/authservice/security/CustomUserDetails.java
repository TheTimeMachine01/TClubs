package com.ashish.clubs.authservice.security;

import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User; // Spring Security's User class
import java.util.Collection;


@Getter
public class CustomUserDetails extends User {
    private final String userId; // Store the actual user ID from the database

    public CustomUserDetails(String userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities); // username is typically email
        this.userId = userId;
    }

}

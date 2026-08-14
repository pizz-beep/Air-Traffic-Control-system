package com.atc.dto;

import com.atc.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
    private Long userId;
    private String name;
    private String email;
    private String role;

    public UserDto(User user) {
        this.userId = user.getUserId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole().name();
    }
}

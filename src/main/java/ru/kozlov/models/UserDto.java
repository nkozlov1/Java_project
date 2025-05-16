package ru.kozlov.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private Role role;

    public UserDto(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}

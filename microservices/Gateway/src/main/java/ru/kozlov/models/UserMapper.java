package ru.kozlov.models;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {
    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getPassword(), user.getRole());
    }

    public User toDao(UserDto user) {
        return new User(user.getId(), user.getUsername(), user.getPassword(), user.getRole());
    }
}


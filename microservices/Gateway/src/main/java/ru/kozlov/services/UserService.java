package ru.kozlov.services;

import ru.kozlov.models.UserDto;

import java.util.List;

public interface UserService {
    UserDto findByUsername(String username);

    UserDto save(UserDto user);

    List<UserDto> getAll();
}

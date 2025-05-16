package ru.kozlov.mappers;

import lombok.experimental.UtilityClass;
import ru.kozlov.models.CatOwner;
import ru.kozlov.models.CatOwnerDto;
import ru.kozlov.models.User;
import ru.kozlov.models.UserDto;

@UtilityClass
public class UserMapper {
    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getPassword(), user.getRole());
    }

    public User toDao(UserDto user) {
        return new User(user.getId(), user.getUsername(), user.getPassword(), user.getRole());
    }
}


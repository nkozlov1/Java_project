package ru.kozlov.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kozlov.exceptions.UserAlreadyExist;
import ru.kozlov.exceptions.UserNotFoundException;
import ru.kozlov.mappers.UserMapper;
import ru.kozlov.models.User;
import ru.kozlov.models.UserDto;
import ru.kozlov.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }


    public UserDto findByUsername(String username) {
        return userRepository.findByUsername(username).map(UserMapper::toDto).orElseThrow(
                () -> new UserNotFoundException(username)
        );
    }


    public UserDto save(UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new UserAlreadyExist(userDto.getUsername());
        }
        User user = UserMapper.toDao(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User resultUser = userRepository.save(user);
        return UserMapper.toDto(resultUser);
    }


    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }
}

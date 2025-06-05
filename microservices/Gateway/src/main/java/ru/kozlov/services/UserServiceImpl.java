package ru.kozlov.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;
import ru.kozlov.models.UserDto;
import ru.kozlov.exeptions.UserAlreadyExist;
import ru.kozlov.exeptions.UserNotFoundException;
import ru.kozlov.models.User;
import ru.kozlov.models.UserMapper;
import ru.kozlov.repositories.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.kafkaTemplate = kafkaTemplate;
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

    @KafkaListener(topics = "get_current_user_request", groupId = "user-group")
    public void getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        kafkaTemplate.send("get_current_user_response", user.getId());
    }
}

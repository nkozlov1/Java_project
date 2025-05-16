package ru.kozlov.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kozlov.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

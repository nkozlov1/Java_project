package ru.kozlov.exceptions;

public class UserAlreadyExist extends RuntimeException{
    public UserAlreadyExist(String username) {
        super("User already exist: " + username);
    }
}

package ru.kozlov.exceptions;

public class PermissionNotAllowed extends RuntimeException {
    public PermissionNotAllowed(String username) {
        super("Not allowed permission for this cat:" + username);
    }
}

package ru.kozlov.exeptions;

public class PermissionNotAllowed extends RuntimeException {
    public PermissionNotAllowed(String username) {
        super("Not allowed permission for this cat:" + username);
    }
}

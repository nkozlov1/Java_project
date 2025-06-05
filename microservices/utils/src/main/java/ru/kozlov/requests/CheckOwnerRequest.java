package ru.kozlov.requests;

public record CheckOwnerRequest(Long ownerId, Long userId) {
}

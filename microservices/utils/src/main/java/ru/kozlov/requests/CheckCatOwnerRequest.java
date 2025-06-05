package ru.kozlov.requests;

public record CheckCatOwnerRequest(Long catId, Long ownerId) {
}

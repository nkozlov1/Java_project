package ru.kozlov.responses;

import ru.kozlov.dtos.CatOwnerDto;

import java.util.List;

public record OwnerListResponse(CatOwnerDto[] owners) {
}

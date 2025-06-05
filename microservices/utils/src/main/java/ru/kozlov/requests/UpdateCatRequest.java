package ru.kozlov.requests;

import ru.kozlov.dtos.CatDto;

public record UpdateCatRequest(Long id, CatDto dto) {
}

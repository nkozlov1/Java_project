package ru.kozlov.responses;

import ru.kozlov.dtos.CatDto;

import java.util.List;

public record CatListResponse(List<CatDto> cats) {
}

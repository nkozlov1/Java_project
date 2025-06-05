package ru.kozlov.dtos;

import java.time.LocalDate;

public record CatFilter(String nameContains, LocalDate birthDate, String breed, ColorsDto color) {
}

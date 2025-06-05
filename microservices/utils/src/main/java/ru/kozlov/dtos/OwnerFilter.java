package ru.kozlov.dtos;

import java.time.LocalDate;

public record OwnerFilter(String nameContains, LocalDate birthDate) {
}

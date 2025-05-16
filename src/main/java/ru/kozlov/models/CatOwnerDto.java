package ru.kozlov.models;

import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO владельца кота")
public class CatOwnerDto {
    @Schema(example = "10", description = "Идентификатор вледельца", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(example = "Алексей", description = "Имя владельца")
    private String name;

    @Schema(example = "1990-05-01", type = "string", format = "date", description = "Дата рождения")
    private LocalDate birthDate;

    public CatOwnerDto(String name, String birthDate) {
        this.name = name;
        this.birthDate = LocalDate.parse(birthDate);
    }
}
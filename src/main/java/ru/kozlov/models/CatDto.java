package ru.kozlov.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO кота")
public class CatDto {
    @Schema(example = "1", description = "Идентификатор кота", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(example = "Барсик", description = "Имя кота")
    private String name;

    @Schema(example = "2020-01-01", type = "string", format = "date", description = "Дата рождения")
    private LocalDate birthDate;

    @Schema(example = "Siam", description = "Порода")
    private String breed;

    @Schema(description = "Цвет кота")
    private ColorsDto color;

    public CatDto(String name, String birthDate, String breed, ColorsDto color) {
        this.name = name;
        this.birthDate = LocalDate.parse(birthDate);
        this.breed = breed;
        this.color = color;
    }


}
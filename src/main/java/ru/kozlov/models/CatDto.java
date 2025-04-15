package ru.kozlov.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatDto {
    private Long id;
    private String name;
    private LocalDate birthDate;
    private String breed;
    private ColorsDto color;

    public CatDto(String name, String birthDate, String breed, ColorsDto color) {
        this.name = name;
        this.birthDate = LocalDate.parse(birthDate);
        this.breed = breed;
        this.color = color;
    }


}
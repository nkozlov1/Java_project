package ru.kozlov.models;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatOwnerDto {
    private Long id;
    private String name;
    private LocalDate birthDate;

    public CatOwnerDto(String name, String birthDate) {
        this.name = name;
        this.birthDate = LocalDate.parse(birthDate);
    }
}
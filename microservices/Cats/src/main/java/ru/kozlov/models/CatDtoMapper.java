package ru.kozlov.models;

import lombok.experimental.UtilityClass;
import ru.kozlov.dtos.CatDto;


@UtilityClass
public class CatDtoMapper {
    public CatDto toDto(Cat cat) {
        return new CatDto(cat.getId(), cat.getName(), cat.getBirthDate(), cat.getBreed(), ColorsDtoMapper.toDto(cat.getColor()));
    }

    public Cat toDao(CatDto cat) {
        return new Cat(cat.getId(), cat.getName(), cat.getBirthDate(), cat.getBreed(), ColorsDtoMapper.toDao(cat.getColor()));
    }
}

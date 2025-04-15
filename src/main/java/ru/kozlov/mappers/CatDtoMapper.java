package ru.kozlov.mappers;

import lombok.experimental.UtilityClass;
import ru.kozlov.models.Cat;
import ru.kozlov.models.CatDto;


@UtilityClass
public class CatDtoMapper {
    public CatDto toDto(Cat cat) {
        return new CatDto(cat.getId(), cat.getName(), cat.getBirthDate(), cat.getBreed(), ColorsDtoMapper.toDto(cat.getColor()));
    }

    public Cat toDao(CatDto cat) {
        return new Cat(cat.getId(), cat.getName(), cat.getBirthDate(), cat.getBreed(), ColorsDtoMapper.toDao(cat.getColor()));
    }
}

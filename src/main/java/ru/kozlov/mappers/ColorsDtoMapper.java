package ru.kozlov.mappers;

import lombok.experimental.UtilityClass;
import ru.kozlov.models.Colors;
import ru.kozlov.models.ColorsDto;


@UtilityClass
public class ColorsDtoMapper {
    public Colors toDao(ColorsDto colorsDto) {
        return switch (colorsDto) {
            case Black -> Colors.Black;
            case White -> Colors.White;
            case Brown -> Colors.Brown;
            case Green -> Colors.Green;
            case Grey -> Colors.Grey;
        };
    }

    public ColorsDto toDto(Colors colors) {
        return switch (colors) {
            case Black -> ColorsDto.Black;
            case White -> ColorsDto.White;
            case Brown -> ColorsDto.Brown;
            case Green -> ColorsDto.Green;
            case Grey -> ColorsDto.Grey;
        };
    }

}

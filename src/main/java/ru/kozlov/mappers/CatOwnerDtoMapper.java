package ru.kozlov.mappers;

import lombok.experimental.UtilityClass;
import ru.kozlov.models.CatOwner;
import ru.kozlov.models.CatOwnerDto;

@UtilityClass
public class CatOwnerDtoMapper {
    public CatOwnerDto toDto(CatOwner owner) {
        return new CatOwnerDto(owner.getId(), owner.getName(), owner.getBirthDate());
    }

    public CatOwner toDao(CatOwnerDto owner) {
        return new CatOwner(owner.getId(), owner.getName(), owner.getBirthDate());
    }
}

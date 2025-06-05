package ru.kozlov.models;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import ru.kozlov.dtos.OwnerFilter;

@UtilityClass
public class CatOwnerSpecifications {
    public Specification<CatOwner> fromFilter(OwnerFilter filter) {
        return Specification
                .where(nameContains(filter))
                .and(birthDateEquals(filter));
    }


    private Specification<CatOwner> nameContains(OwnerFilter filter) {
        return (root, q, cb) ->
                StringUtils.hasText(filter.nameContains())
                        ? cb.like(root.get("name"), "%" + filter.nameContains() + "%")
                        : null;
    }

    private Specification<CatOwner> birthDateEquals(OwnerFilter filter) {
        return (root, q, cb) ->
                filter.birthDate() != null
                        ? cb.equal(root.get("birthDate"), filter.birthDate())
                        : null;
    }
}
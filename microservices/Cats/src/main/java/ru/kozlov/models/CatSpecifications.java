package ru.kozlov.models;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import ru.kozlov.dtos.CatFilter;
import ru.kozlov.dtos.OwnerFilter;

@UtilityClass
public class CatSpecifications {
    public Specification<Cat> fromFilter(CatFilter filter) {
        return Specification.where(nameContainSpec(filter))
                .and(birthDateSpec(filter))
                .and(breedSpec(filter))
                .and(colorSpec(filter));
    }

    private Specification<Cat> nameContainSpec(CatFilter filter) {
        return ((root, query, cb) -> StringUtils.hasText(filter.nameContains())
                ? cb.like(root.get("name"), "%" + filter.nameContains() + "%")
                : null);
    }

    private Specification<Cat> birthDateSpec(CatFilter filter) {
        return ((root, query, cb) ->
                filter.birthDate() != null
                        ? cb.equal(root.get("birthDate"), filter.birthDate())
                        : null);
    }

    private Specification<Cat> breedSpec(CatFilter filter) {
        return ((root, query, cb) -> StringUtils.hasText(filter.breed())
                ? cb.equal(root.get("breed"), filter.breed())
                : null);
    }

    public Specification<Cat> colorSpec(CatFilter filter) {
        return ((root, query, cb) ->
                filter.color() != null
                        ? cb.equal(root.get("color"), filter.color())
                        : null);
    }
}

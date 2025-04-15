package ru.kozlov.models;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

public record OwnerFilter(String nameContains, LocalDate birthDate) {
    public Specification<CatOwner> toSpecification() {
        return Specification.where(nameContainSpec())
                .and(birthDateSpec());
    }

    private Specification<CatOwner> nameContainSpec() {
        return ((root, query, cb) -> StringUtils.hasText(nameContains)
                ? cb.like(root.get("name"), "%" + nameContains + "%")
                : null);
    }

    private Specification<CatOwner> birthDateSpec() {
        return ((root, query, cb) ->
                birthDate != null
                        ? cb.equal(root.get("birthDate"), birthDate)
                        : null);
    }
}

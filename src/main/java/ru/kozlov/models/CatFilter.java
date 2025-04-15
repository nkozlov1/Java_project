package ru.kozlov.models;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

public record CatFilter(String nameContains, LocalDate birthDate, String breed, Colors color) {
    public Specification<Cat> toSpecification() {
        return Specification.where(nameContainSpec())
                .and(birthDateSpec())
                .and(breedSpec())
                .and(colorSpec());
    }

    private Specification<Cat> nameContainSpec() {
        return ((root, query, cb) -> StringUtils.hasText(nameContains)
                ? cb.like(root.get("name"), "%" + nameContains + "%")
                : null);
    }

    private Specification<Cat> birthDateSpec() {
        return ((root, query, cb) ->
                birthDate != null
                        ? cb.equal(root.get("birthDate"), birthDate)
                        : null);
    }

    private Specification<Cat> breedSpec() {
        return ((root, query, cb) -> StringUtils.hasText(breed)
                ? cb.equal(root.get("breed"), breed)
                : null);
    }

    public Specification<Cat> colorSpec() {
        return ((root, query, cb) ->
                color != null
                        ? cb.equal(root.get("color"), color)
                        : null);
    }
}

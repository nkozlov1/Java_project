package ru.kozlov.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.kozlov.models.CatOwner;

import java.util.Optional;


@Repository
public interface CatOwnerRepository extends JpaRepository<CatOwner, Long>, JpaSpecificationExecutor<CatOwner> {
    boolean existsByUserId(Long userId);
    Optional<CatOwner> findByUserId(Long userId);
}
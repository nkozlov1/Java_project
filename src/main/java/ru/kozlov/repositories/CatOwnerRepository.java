package ru.kozlov.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.kozlov.models.CatOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface CatOwnerRepository extends JpaRepository<CatOwner, Long>, JpaSpecificationExecutor<CatOwner> {
}
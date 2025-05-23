package ru.kozlov.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.kozlov.models.Cat;

import java.util.List;
import java.util.Set;

@Repository
public interface CatRepository extends JpaRepository<Cat, Long>, JpaSpecificationExecutor<Cat> {
    Set<Cat> findCatsByFriendsId(long id);

    List<Cat> findCatsByOwnerId(long id);
}
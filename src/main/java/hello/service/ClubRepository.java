package hello.service;

import hello.entity.ClubEntity;
import org.springframework.data.repository.CrudRepository;

public interface ClubRepository extends CrudRepository<ClubEntity, Long> {
    ClubEntity findFirstById(Long id);
    ClubEntity findFirstByName(String name);
}

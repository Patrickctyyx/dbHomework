package hello.service;

import hello.entity.ClubEntity;
import hello.entity.UserClubEntity;
import hello.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserClubRepository extends CrudRepository<UserClubEntity, Long> {
    UserClubEntity findFirstByUserAndClub(UserEntity user, ClubEntity club);
}

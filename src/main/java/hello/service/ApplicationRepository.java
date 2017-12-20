package hello.service;

import hello.entity.ApplicationEntity;
import hello.entity.ClubEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ApplicationRepository extends CrudRepository<ApplicationEntity, Long> {
    ApplicationEntity findFirstById(Long id);
    ApplicationEntity findFirstByPhone(String phone);
    ApplicationEntity findFirstByEmail(String email);
    List<ApplicationEntity> findByName(String name);
    List<ApplicationEntity> findByClubOrderByCredAtDesc(ClubEntity club);
    List<ApplicationEntity> findAllByOrderByCredAtDesc();
}
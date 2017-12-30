package hello.service;

import hello.entity.ActivityEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ActivityRepository extends CrudRepository<ActivityEntity, Long> {
    List<ActivityEntity> findAllByOrderByLastModifiedDesc();
    ActivityEntity findFirstById(Long id);
}

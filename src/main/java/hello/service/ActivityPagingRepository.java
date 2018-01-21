package hello.service;

import hello.entity.ActivityEntity;
import hello.entity.ClubEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ActivityPagingRepository extends PagingAndSortingRepository<ActivityEntity, Long> {
    Page<ActivityEntity> findByClub(ClubEntity club, Pageable pageable);
}

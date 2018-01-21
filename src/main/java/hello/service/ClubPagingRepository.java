package hello.service;

import hello.entity.ClubEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ClubPagingRepository extends PagingAndSortingRepository<ClubEntity, Long> {
}

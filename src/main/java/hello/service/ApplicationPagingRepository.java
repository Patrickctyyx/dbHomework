package hello.service;

import hello.entity.ApplicationEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ApplicationPagingRepository extends PagingAndSortingRepository<ApplicationEntity, Long> {
}

package hello.service;

import hello.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    UserEntity findFirstById(Long id);
    UserEntity findFirstByPhone(String phone);
    UserEntity findFirstByWxID(String wxID);
    UserEntity findFirstByEmail(String email);
    UserEntity findFirstByQq(String qq);
    UserEntity findFirstByWechat(String wechat);
}

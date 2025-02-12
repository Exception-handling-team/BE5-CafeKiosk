package programmers.cafe.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import programmers.cafe.member.domain.entity.Users;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    boolean existsByLoginId(String loginId);

    Optional<Users> findByLoginId(String loginId);
}

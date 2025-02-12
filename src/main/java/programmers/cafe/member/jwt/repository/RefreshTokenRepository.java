package programmers.cafe.member.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import programmers.cafe.member.jwt.domain.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByKey(String name);
}

package ams.repository;

import ams.model.entity.RefreshToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends BaseRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    boolean existsByTokenAndDeletedFalse(String token);

}

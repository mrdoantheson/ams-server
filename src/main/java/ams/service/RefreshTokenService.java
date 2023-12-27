package ams.service;


import ams.model.entity.RefreshToken;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public interface RefreshTokenService extends BaseService<RefreshToken, Long> {

   Optional<RefreshToken> findByToken(String token);

   void deleteByToken(String token);

}

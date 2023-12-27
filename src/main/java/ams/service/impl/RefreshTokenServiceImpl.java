package ams.service.impl;

import ams.exception.ResourceNotFoundException;
import ams.model.entity.RefreshToken;
import ams.repository.RefreshTokenRepository;
import ams.service.RefreshTokenService;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class RefreshTokenServiceImpl
        extends BaseServiceImpl<RefreshToken, Long, RefreshTokenRepository>
        implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public void deleteByToken(String token) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(token);
        RefreshToken refreshToken = refreshTokenOpt.orElseThrow(ResourceNotFoundException::new);
        refreshToken.setDeleted(true);
        refreshTokenRepository.save(refreshToken);
    }


}

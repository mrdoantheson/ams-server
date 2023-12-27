package ams.resource;

import ams.exception.TokenExpiredException;
import ams.model.dto.BaseResponseDto;
import ams.model.dto.LoginRequestDto;
import ams.model.dto.LoginResponseDto;
import ams.model.dto.TokenRefreshRequestDto;
import ams.model.entity.RefreshToken;
import ams.security.SecurityUtil;
import ams.security.TokenProvider;
import ams.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Slf4j
public class LoginResource extends BaseResource {
    public static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LoginResource.class);

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.security.secret-key}")
    private String secretKey;

    @Value("${app.security.jwtExpirationS}")
    private Long jwtExpirationS;

    @Value("${app.security.secret-key-refresh}")
    private String secretKeyRefresh;

    @Value("${app.security.jwtRefreshExpirationS}")
    private Long jwtRefreshExpirationS;

    public LoginResource(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, RefreshTokenService refreshTokenService) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/api/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequestDto.getUsername(),
                loginRequestDto.getPassword()
        );

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.generateToken(authentication, secretKey, jwtExpirationS);

        String refreshToken = tokenProvider.generateToken(authentication, secretKeyRefresh, jwtRefreshExpirationS);

        RefreshToken refreshTokenEntity = new RefreshToken(refreshToken);

        refreshTokenService.createOrUpdate(refreshTokenEntity);

        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(loginRequestDto.getUsername())
                .role(SecurityUtil.getRoleCurrentUserLogin().orElseThrow(RuntimeException::new).name())
                .build();

        return ResponseEntity.ok(loginResponseDto);
    }


    @PostMapping("/api/refreshtoken")
    public ResponseEntity<BaseResponseDto> refreshToken(@Valid @RequestBody TokenRefreshRequestDto request) throws TokenExpiredException {
        String requestRefreshToken = request.getRefreshToken();

        Authentication authentication1 = tokenProvider.getAuthentication(requestRefreshToken);
        SecurityContextHolder.getContext().setAuthentication(authentication1);

        Optional<RefreshToken> refreshToken = refreshTokenService.findByToken(requestRefreshToken);

        if (refreshToken.isEmpty()) {
            return notFound("login.resource.refreshtoken.dont.exist");
        }

        RefreshToken refreshToken1 = refreshToken.get();

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication == null) {
            throw new TokenExpiredException("login.resource.refreshtoken.expired");
        }

        String accessToken = tokenProvider.generateToken(authentication, secretKey, jwtExpirationS);
        String refreshTokenNew = tokenProvider.generateToken(authentication, secretKeyRefresh, jwtRefreshExpirationS);


        refreshToken1.setToken(refreshTokenNew);

        refreshTokenService.createOrUpdate(refreshToken1);

        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenNew)
                .username(SecurityUtil.getCurrentUserLogin().orElseThrow(RuntimeException::new))
                .role(SecurityUtil.getRoleCurrentUserLogin().orElseThrow(RuntimeException::new).name())
                .build();

        return success(loginResponseDto, "ok");
    }


    @PostMapping("/api/logout")
    public ResponseEntity<BaseResponseDto> logout(@Valid @RequestBody TokenRefreshRequestDto request) {
        String refreshToken = request.getRefreshToken();
        refreshTokenService.deleteByToken(refreshToken);

        return success("login.resource.success!");
    }


}

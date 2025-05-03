package io.github.jann1325.service;

import io.github.jann1325.domain.LoginToken;
import io.github.jann1325.domain.UserInfo;
import io.github.jann1325.dto.UserDTO;
import io.github.jann1325.exception.ApiException;
import io.github.jann1325.repository.LoginTokenRepository;
import io.github.jann1325.repository.UserInfoRepository;
import io.github.jann1325.response.ApiResponse;
import io.github.jann1325.response.ErrorCode;
import io.github.jann1325.utils.ApiResponseUtils;
import io.github.jann1325.utils.EmailUtils;
import io.github.jann1325.utils.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class AuthService {

    @Autowired
    private LoginTokenRepository loginTokenRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private EmailUtils emailUtils;

    // 登入
    public ApiResponse<String> login(UserDTO.LoginDTO loginDTO) {
        UserInfo user = userInfoRepository.findByEmail(loginDTO.getEmail());
        if (user != null) {
            try {
                sendVerificationEmail(user.getId());
                return ApiResponseUtils.success("verifyEmail");
            } catch (ApiException e) {
                return ApiResponseUtils.fail(e.getErrorCode().getCode(), e.getMessage(), null);
            }
        } else {
            return ApiResponseUtils.fail(ErrorCode.EMAIL_NOT_REGISTERED.getCode(),"NEED_REGISTER", null);
        }
    }

    // 寄發郵件驗證信
    public void sendVerificationEmail(Integer userId) {
        Optional<UserInfo> user = userInfoRepository.findById(userId);
        if (user.isPresent()) {
            String email = user.get().getEmail();
            // 查詢此 email 最近一筆 token 是否超過一分鐘，超過一分鐘才能再次請求 token
            LoginToken latestToken = loginTokenRepository.findTopByEmailOrderByCreatedTimeDesc(email);
            if (latestToken != null && latestToken.getCreatedTime() != null) {
                long secondsSinceLastEmail = ChronoUnit.SECONDS.between(latestToken.getCreatedTime(), LocalDateTime.now());
                if (secondsSinceLastEmail < 60) {
                    throw new ApiException(ErrorCode.TOO_MANY_REQUESTS, "請等待 " + (60 - secondsSinceLastEmail) + " 秒後再試");

                }
            }

            String token = UUID.randomUUID().toString();
            LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

            LoginToken loginToken = new LoginToken();
            loginToken.setToken(token);
            loginToken.setEmail(email);
            loginToken.setExpiresAt(expiry);
            loginToken.setCreatedTime(LocalDateTime.now());
            loginTokenRepository.save(loginToken);

            String verificationUrl = "http://localhost:9000/#/?token=" + token;

            StringBuilder content = new StringBuilder();
            content.append("<p>您好，</p>");
            content.append("<p>請點擊以下連結完成登入驗證：</p>");
            content.append("<p><a href='").append(verificationUrl).append("'>立即登入</a></p>");
            content.append("<p>連結 10 分鐘內有效。</p>");
            content.append("<p>若您未發出此請求，請忽略此信。</p>");

            emailUtils.sendEmail(
                    email,
                    "【PubiPet 寵物商城】您的登入驗證連結",
                    content.toString()
            );
            log.info("驗證信已發送至 {}", email);
        }
    }

    // 驗證郵件 token 是否有效
    public String verifyLoginToken(String emailToken) {
        Optional<LoginToken> loginToken = loginTokenRepository.findByTokenAndUsedIsFalse(emailToken);
        if (!loginToken.isPresent()) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "驗證連結無效，請重新登入");

        }
        if (loginToken.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "驗證連結已過期，請重新登入");
        }

        String email = loginToken.get().getEmail();
        UserInfo user = userInfoRepository.findByEmail(email);
        if (user == null) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }

        // 建立 JWT token
        Map<String, Object> claims = new HashMap<>();
        if (user != null) {
            claims.put("id", user.getId());
            claims.put("email", user.getEmail());
            claims.put("userName", user.getUserName());
            claims.put("token", JwtUtils.generateJwt(claims));
            return claims.get("token").toString();
        } else {
            return "LoginFail";
        }
    }

    // 驗證JWT token
    public void validateToken(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Map<String, Object> claims = JwtUtils.parseJwt(token);
        Integer id = Integer.valueOf(claims.get("id").toString());
        if(!JwtUtils.validateToken(token, id)){
            throw new ApiException(ErrorCode.UNAUTHORIZED, "驗證失敗");
        }
    }
}

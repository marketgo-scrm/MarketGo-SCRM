package com.easy.marketgo.web.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.easy.marketgo.common.utils.DateFormatUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.Date;


@ConfigurationProperties(prefix = "mktgo.jwt")
@Component
@Log4j2
public class JwtUtils {

    private String secret;
    private long expire;
    private String apiToken;
    private String refreshToken;


    /**
     * 创建token
     */
    public String generateToken(String username) {

        LocalDateTime localDateTime = LocalDateTime.now().plusDays(expire);
        log.info("时间：{}----------------------", localDateTime);
        String token = JWT.create()
                          .withClaim("sys_username", username)
                          .withExpiresAt(DateFormatUtils.convertTo(localDateTime))
                          .sign(Algorithm.HMAC256(secret));
        log.info("用户：{} =====> token：{}", username, token);
        return token;
    }

    /**
     * 创建token
     */
    public String generateRefreshToken(String username) {

        LocalDateTime localDateTime = LocalDateTime.now().plusDays(expire);
        log.info("时间：{}----------------------", localDateTime);
        String token = JWT.create()
                          .withClaim("sys_username", username)
                          .withExpiresAt(DateFormatUtils.convertTo(localDateTime))
                          .sign(Algorithm.HMAC256(secret));
        log.info("用户：{} =====> token：{}", username, token);
        return token;
    }

    /**
     * 校验token是否正确
     */
    public boolean verify(String token, String username) throws TokenExpiredException {

        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                                  .withClaim("sys_username", username)
                                  .build();
        verifier.verify(token);
        return true;
    }

    /**
     * 解析token，获取用户名
     */
    public String getUsername(String token) {

        DecodedJWT decode = JWT.decode(token);
        return decode.getClaim("sys_username").asString();
    }

    /**
     * 验证token是否失效
     *
     * @param token
     * @return true:过期   false:没过期
     */
    public boolean isExpired(String token) {

        try {
            final Date expiration = getExpiration(token);
            return expiration.before(new Date());
        } catch (JWTDecodeException JWT) {
            return true;
        }
    }

    /**
     * 获取jwt失效时间
     */
    public Date getExpiration(String token) {

        return JWT.decode(token).getExpiresAt();
    }

    /**
     * token是否过期
     *
     * @return true：过期
     */
    public boolean isTokenExpired(Date expiration) {

        return expiration.before(new Date());
    }


    public long getExpire() {

        return expire;
    }

    public void setExpire(long expire) {

        this.expire = expire;
    }

    public String getApiToken() {

        return apiToken;
    }

    public void setApiToken(String apiToken) {

        this.apiToken = apiToken;
    }

    public String getRefreshToken() {

        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {

        this.refreshToken = refreshToken;
    }

    public String getSecret() {

        return secret;
    }

    public void setSecret(String secret) {

        this.secret = secret;
    }

}
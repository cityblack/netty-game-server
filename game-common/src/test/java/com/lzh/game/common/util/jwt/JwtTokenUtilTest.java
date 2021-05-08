package com.lzh.game.common.util.jwt;

import io.jsonwebtoken.impl.DefaultClaims;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

@Slf4j
public class JwtTokenUtilTest {

    @Test
    public void validateToken() throws InterruptedException {
        String secret = "as123";
        String token = JwtTokenUtil.createToken(new DefaultClaims(), secret, 5);

        JwtTokenUtil.ValidateResult result = JwtTokenUtil.validateToken(token, secret);
        log.info("{}", result);

        Thread.sleep(TimeUnit.SECONDS.toMillis(5));
        log.info("{}", JwtTokenUtil.validateToken(token, secret));
    }
}

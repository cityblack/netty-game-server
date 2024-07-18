package com.lzh.game.framework.gateway;

import com.lzh.game.framework.gateway.config.GateConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author zehong.l
 * @since 2024-07-18 15:50
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import({GateConfiguration.class})
public @interface EnableGateway {
}

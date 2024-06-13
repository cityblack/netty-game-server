package com.lzh.game.framework.socket.starter;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * use tcp server
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({TcpServerConfiguration.class})
public @interface EnableTcpServer {
}

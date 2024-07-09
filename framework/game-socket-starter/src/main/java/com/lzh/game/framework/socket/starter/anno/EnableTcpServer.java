package com.lzh.game.framework.socket.starter.anno;

import com.lzh.game.framework.socket.starter.config.TcpServerConfiguration;
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

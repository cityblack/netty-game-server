package com.lzh.game.framework.config;

import com.lzh.game.framework.FrameworkConfiguration;
import com.lzh.socket.starter.TcpServerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * use tcp server
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({FrameworkConfiguration.class, TcpServerConfiguration.class})
public @interface EnableTcpServer {
}

package com.lzh.game.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "game.client")
public class ClientProperties {

    private int port = 8080;

    private String host = "localhost";
}

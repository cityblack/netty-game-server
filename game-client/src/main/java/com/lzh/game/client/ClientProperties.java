package com.lzh.game.client;

import com.lzh.game.socket.GameSocketProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "game.client")
public class ClientProperties extends GameSocketProperties {

}

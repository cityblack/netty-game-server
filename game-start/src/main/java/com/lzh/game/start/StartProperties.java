package com.lzh.game.start;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "game.setting")
public class StartProperties {

    private int gameId = 1001;

}

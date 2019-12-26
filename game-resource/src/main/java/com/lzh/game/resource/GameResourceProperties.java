package com.lzh.game.resource;

import com.lzh.game.resource.data.DefaultNameStrategyStandard;
import com.lzh.game.resource.data.ResourceNameStrategyStandard;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "game.resource")
public class GameResourceProperties {

    private Class<? extends ResourceNameStrategyStandard> nameStrategyStandard = DefaultNameStrategyStandard.class;

    private String resourceScannerPath = "com.lzh.game";
}

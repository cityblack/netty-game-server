package com.lzh.game.framework.resource.config;

import com.lzh.game.framework.resource.data.DefaultNameStrategyStandard;
import com.lzh.game.framework.resource.data.ResourceNameStrategyStandard;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "game.resource")
public class GameResourceProperties {

    private Class<? extends ResourceNameStrategyStandard> nameStrategyStandard = DefaultNameStrategyStandard.class;

    private String[] resourceScannerPath = new String[]{"com.lzh.game.framework.resource"};
}

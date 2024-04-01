package com.lzh.game.framework.gateway;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("game.gateway")
public class GatewayProperties {

    @Getter
    @Setter
    private List<String> serverAddress = new ArrayList<>();

}

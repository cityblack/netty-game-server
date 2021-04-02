package com.lzh.game.start.server;

import org.springframework.core.Ordered;

/**
 * 提供该接口初始化业务, 禁止使用InitBean接口做业务操作
 */
public interface AfterServerStartInit extends Ordered, Comparable<AfterServerStartInit> {

    void init();

    @Override
    default int compareTo(AfterServerStartInit o) {
        return o.getOrder() > this.getOrder() ? 1 : o.getOrder() == this.getOrder() ? 0 : -1 ;
    }

    /**
     * Execute ordered
     * @return
     */
    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}

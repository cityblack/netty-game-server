package com.lzh.game.framework.socket.utils;

import java.util.function.Consumer;

public interface SessionMonitor {

    /**
     * Need monitor ip
     * @param ip
     * @param port
     * @param config
     */
    void register(String ip, int port, SeasonMonitorConfig config, Consumer<String> notConnCall);

    /**
     *
     * @param address
     * @param config
     */
    void register(String address, SeasonMonitorConfig config, Consumer<String> notConnCall);


    void unRegister(String address, SeasonMonitorConfig config);
}

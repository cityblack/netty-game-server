package com.lzh.game.framework.socket.core.session.monitor;

import java.util.function.Consumer;

public interface ConnectMonitor {

    /**
     * Need monitor ip
     * @param ip
     * @param port
     * @param config
     */
    void register(String ip, int port, ConnectMonitorConfig config, Consumer<String> notConnCall);

    /**
     *
     * @param address
     * @param config
     */
    void register(String address, ConnectMonitorConfig config, Consumer<String> notConnCall);

    void unRegister(String ip, int port);


    void unRegister(String address);
}

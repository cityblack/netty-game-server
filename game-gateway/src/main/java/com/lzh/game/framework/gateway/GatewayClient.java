package com.lzh.game.framework.gateway;

import com.lzh.game.framework.socket.GameSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.client.GameTcpClient;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionManage;
import com.lzh.game.framework.socket.utils.SeasonMonitorConfig;
import com.lzh.game.framework.socket.utils.SessionMonitorMange;

import java.util.List;
import java.util.function.Consumer;

/**
 * Read remote server from properties
 * Read servers from register center?(nacos)
 */
public class GatewayClient extends GameTcpClient {

    private GatewayProperties properties;

    public GatewayClient(GameSocketProperties socketProperties, GatewayProperties properties, SessionManage<Session> sessionManage) {
        super(socketProperties, new SessionMonitorMange<>(sessionManage));
        this.properties = properties;
    }

    @Override
    public SessionMonitorMange<Session> getSessionManage() {
        return (SessionMonitorMange<Session>) super.getSessionManage();
    }

    @Override
    public void start() {
        checkConfig();
        super.start();
        afterStart();
    }

    public static boolean isIP(String ip) {
        boolean doip = ip.matches("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])" +
                "(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");
        return doip;
    }

    protected void checkConfig() {
        if (!this.properties.getServerAddress().isEmpty()) {
            for (String address : this.properties.getServerAddress()) {
                String[] value = address.split(":");
                if (!isIP(value[0])) {
                    throw new IllegalArgumentException("Config error, server address is invalid");
                }
                try {
                    Integer.parseInt(value[1]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Config error, server address is invalid", e);
                }
            }
        }
    }

    protected void afterStart() {
        List<String> addresses = properties.getServerAddress();
        for (String address : addresses) {
            doConnect(address);
            getSessionManage().register(address, new SeasonMonitorConfig(), RE_CONNECT);
        }
    }

    protected void doConnect(String address) {
        try {
            String[] value = address.split(":");
            conn(value[0], Integer.parseInt(value[1]), getProperties().getRequestTimeout());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected final Consumer<String> RE_CONNECT = this::doConnect;

}

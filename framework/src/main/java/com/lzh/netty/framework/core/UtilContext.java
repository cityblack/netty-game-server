package com.lzh.netty.framework.core;

import com.lzh.netty.framework.core.util.SessionUtil;
import com.lzh.netty.socket.protocol.session.GameSession;
import com.lzh.netty.socket.protocol.session.SessionManage;
import com.lzh.netty.socket.protocol.session.manage.GameSessionManage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 承接
 * session player工具 注入相应的值
 */
@Slf4j
public class UtilContext implements ApplicationContextAware,InitializingBean {

    private SessionManage<GameSession> sessionManage;
    private PlayerManage playerManage;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            sessionManage = applicationContext.getBean(GameSessionManage.class);
            playerManage = applicationContext.getBean(PlayerManage.class);
        } catch (Exception e) {
            log.error("init fail.");
            throw e;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        settingSessionUtil(playerManage);
    }

    private void settingSessionUtil(PlayerManage playerManage) {
        synchronized (SessionUtil.class) {
            SessionUtil.setPlayerManage(playerManage);
        }
    }
}

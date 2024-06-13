package com.lzh.game.start.gm.service;

import com.lzh.game.start.util.ApplicationUtils;
import com.lzh.game.framework.socket.GameServerSocketProperties;
import com.lzh.game.start.gm.GmFacade;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.framework.socket.core.session.Session;

import com.lzh.game.start.model.player.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Component
public class DefaultGmServiceImpl implements GmService, InitializingBean, ApplicationContextAware, DisposableBean {

    @Autowired
    private GameServerSocketProperties properties;

    private final Map<String, GmHandlerMethod> methodMap = new HashMap<>();

    private ApplicationContext context;

    @Override
    public void doGmMethod(Session session, String methodName, String value) {

        if (!StringUtils.hasText(methodName)) {
            log.debug("Gm request no valid because of the convent name is null");
            return;
        }
        Player player = ApplicationUtils.getBean(PlayerService.class).getPlayer(session);
        String[] valueArr = value.split("\\s");
        //convent first arg must be PlayerEnt
        String key = getKey(methodName, valueArr.length + 1);

        if (!methodMap.containsKey(key)) {
            if (log.isDebugEnabled()) {
                log.debug("Not contain {} gm",methodName);
            }
            return;
        }
        GmHandlerMethod method = methodMap.get(key);
        if (method != null) {
            try {
                method.invokeForValues(player,valueArr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (properties.isOpenGm()) {
            Map<String,Object> beans = loadGmBean(context);
            if (beans.isEmpty()) {
                log.info("Gm isn't defined");
                return;
            }
            synchronized (DefaultGmServiceImpl.class) {
                beans.forEach((k,v) -> parseFacade(v));
            }
        }
    }

    /**
     * Load defined gm bean
     * @param context
     * @return
     */
    private Map<String,Object> loadGmBean(ApplicationContext context) {
        return context.getBeansWithAnnotation(GmFacade.class);
    }

    /***
     * Produce key with convent name and convent args count
     * @param methodName
     * @param argsLen
     * @return
     */
    private String getKey(String methodName, int argsLen) {
        return methodName + "_" + argsLen;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void destroy() throws Exception {
        methodMap.clear();
    }

    private void parseFacade(Object bean) {

        Stream.of(bean.getClass().getDeclaredMethods())
                .forEach(m -> {
                    GmHandlerMethod method = new GmHandlerMethod(bean,m);
                    String key = getKey(method.getMethod().getName(), method.getParamsType().length);
                    if (methodMap.containsKey(key)) {
                        throw new RuntimeException("Gm convent " + method.getMethod().getName() +" not unique, please check the convent name and convent args");
                    } else {
                        methodMap.put(key,method);
                    }
                });
    }
}

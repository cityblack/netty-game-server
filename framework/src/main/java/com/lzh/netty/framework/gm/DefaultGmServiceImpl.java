package com.lzh.netty.framework.gm;

import com.lzh.netty.framework.core.PlayerUtil;
import com.lzh.netty.framework.core.player.Player;
import com.lzh.netty.socket.autoconfig.GameProperties;
import com.lzh.netty.socket.protocol.session.Session;
import com.lzh.netty.start.gm.annotation.GmFacade;

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
    private GameProperties properties;

    private final Map<String, GmHandlerMethod> methodMap = new HashMap<>();

    private ApplicationContext context;

    @Override
    public void doGmMethod(Session session, String methodName, String value) {

        if (!StringUtils.hasText(methodName)) {
            log.debug("Gm request no valid because of the method name is null");
            return;
        }
        Player player = PlayerUtil.getPlayer(session);
        String[] valueArr = value.split("\\s");
        //method first arg must be Player
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
     * Produce key with method name and method args count
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
                    String key = getKey(method.getMethod().getName(), method.getMethodParameters().length);
                    if (methodMap.containsKey(key)) {
                        throw new RuntimeException("Gm method " + method.getMethod().getName() +" not unique, please check the method name and method args");
                    } else {
                        methodMap.put(key,method);
                    }
                });
    }
}

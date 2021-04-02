package com.lzh.game.start.filter;

import com.lzh.game.common.serialization.JsonUtils;
import com.lzh.game.socket.core.invoke.ActionInterceptor;
import com.lzh.game.socket.core.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Slf4j
public class LogInterceptor implements ActionInterceptor {

    @Override
    public boolean intercept(Request request, Method method, Object[] param) {
        if (log.isDebugEnabled()) {
            log.debug("Request:{} param:{}", request.getCmd(), JsonUtils.toJson(param));
        }
        return false;
    }
}

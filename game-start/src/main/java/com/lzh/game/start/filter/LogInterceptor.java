package com.lzh.game.start.filter;

import com.lzh.game.common.serialization.JsonUtil;
import com.lzh.game.socket.dispatcher.action.ActionInterceptor;
import com.lzh.game.socket.exchange.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Slf4j
public class LogInterceptor implements ActionInterceptor {

    @Override
    public boolean intercept(Request request, Method method, Object[] param) {
        if (log.isDebugEnabled()) {
            log.debug("Request:{} param:{}", request.header().getCmd(), JsonUtil.toJSON(param));
        }
        return false;
    }
}

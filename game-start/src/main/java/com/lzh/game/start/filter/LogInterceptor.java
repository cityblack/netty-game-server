package com.lzh.game.start.filter;

import com.lzh.game.framework.socket.core.invoke.support.ActionInterceptor;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.common.method.EnhanceMethodInvoke;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogInterceptor implements ActionInterceptor {

    @Override
    public boolean intercept(Request request, EnhanceMethodInvoke method, Object[] param) {
        if (log.isDebugEnabled()) {
            log.debug("Request:{}", request.getMsgId());
        }
        return false;
    }
}

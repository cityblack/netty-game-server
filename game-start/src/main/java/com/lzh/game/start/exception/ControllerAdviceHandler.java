package com.lzh.game.start.exception;

import com.lzh.game.framework.socket.core.protocol.Response;
import com.lzh.game.framework.socket.starter.anno.ActionAdvice;
import com.lzh.game.framework.socket.starter.anno.ExceptionHandler;
import com.lzh.game.start.model.i18n.I18n;
import com.lzh.game.start.model.i18n.Notify;
import com.lzh.game.start.model.i18n.RequestException;
import com.lzh.game.start.util.PacketUtils;
import lombok.extern.slf4j.Slf4j;

@ActionAdvice
@Slf4j
public class ControllerAdviceHandler {

    @ExceptionHandler({Exception.class})
    public void requestException(Exception ex, Response response) {

        if (ex instanceof RequestException) {
            int i18n = ((RequestException) ex).getI18n();
            if (log.isDebugEnabled()) {
                log.debug("Request error: ", ex);
            }
            PacketUtils.send(response.getRequest().getSession(), Notify.of(i18n));
        } else {
            log.error("", ex);
            PacketUtils.send(response.getRequest().getSession(), Notify.of(I18n.SYS_ERROR));
        }
    }

}

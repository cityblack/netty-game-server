package com.lzh.game.start.exception;

import com.lzh.game.start.cmd.CmdMessage;
import com.lzh.game.start.model.i18n.I18n;
import com.lzh.game.start.model.i18n.Notify;
import com.lzh.game.start.model.i18n.RequestException;
import com.lzh.socket.starter.ActionAdvice;
import com.lzh.socket.starter.ExceptionHandler;
import com.lzh.game.socket.GameResponse;
import lombok.extern.slf4j.Slf4j;

@ActionAdvice
@Slf4j
public class ControllerAdviceHandler {

    @ExceptionHandler({Exception.class})
    public void requestException(Exception ex, GameResponse response) {

        response.setCmd(CmdMessage.SM_NOTIFY);
        if (ex instanceof RequestException) {
            int i18n = ((RequestException) ex).getI18n();
            if (log.isDebugEnabled()) {
                log.debug("Request error: ", ex);
            }

            response.setData(Notify.of(i18n));
        } else {
            log.error("", ex);

            response.setData(Notify.of(I18n.SYS_ERROR));
        }

    }

}

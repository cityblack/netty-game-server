package com.lzh.game.start.model.i18n;

import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import com.lzh.game.start.util.Constant;
import lombok.Data;

@Data
@Protocol(Constant.SYS_ERR_MSG_ID)
public class Notify {

    private int i18nId;

    public static Notify of(int i18nId) {
        Notify notify = new Notify();
        notify.i18nId = i18nId;
        return notify;
    }
}

package com.lzh.game.framework.socket.utils;

import com.lzh.game.framework.socket.bean.RequestTestBean;
import com.lzh.game.framework.socket.core.invoke.Receive;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author zehong.l
 * @since 2024-06-28 17:59
 **/
@Slf4j
class InvokeUtilsTest {

    @Test
    void parseBean() {
        var list = InvokeUtils.parseBean(new RequestTestBean());
        for (InvokeUtils.InvokeModel model : list) {
            System.out.println(model.getValue() + "_" + (Objects.isNull(model.getNewProtoClass()) ? "" : model.getNewProtoClass().getName()));
        }
    }

}
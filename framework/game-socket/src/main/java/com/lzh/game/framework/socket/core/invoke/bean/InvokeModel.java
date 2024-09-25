package com.lzh.game.framework.socket.core.invoke.bean;

import com.lzh.game.framework.socket.core.protocol.message.MessageDefine;
import com.lzh.game.framework.common.method.EnhanceMethodInvoke;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

/**
 * @author zehong.l
 * @since 2024-08-21 15:50
 **/
@Data
@Accessors(chain = true)
public class InvokeModel {

    private short msgId;

    private EnhanceMethodInvoke handlerMethod;
    /**
     * {@link @Protocol}
     */
    private Set<Class<?>> protocol;

    private List<MessageDefine> defines;


    public static InvokeModel of() {
        return new InvokeModel();
    }
}

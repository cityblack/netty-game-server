package com.lzh.game.framework.client.bootstrap;

import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.common.serialization.ProtoBufUtils;
import com.lzh.game.socket.core.invoke.InvokeSupport;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class ResponseDispatcher {

    private InvokeSupport<HandlerMethod> methodSupport;

    public ResponseDispatcher(InvokeSupport<HandlerMethod> methodSupport) {
        this.methodSupport = methodSupport;
    }

    public void doResponse(Response response) {
        //SerializationUtil.deSerialize()
        int cmd = response.cmd();
        HandlerMethod method = methodSupport.getActionHandler(cmd);
        if (Objects.isNull(method)) {
            throw new IllegalArgumentException("Not register the " + cmd + " protocol !!");
        }

        try {
            Object[] params = getArgumentValues(response.byteData(), method);
            method.doInvoke(params);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("{} response error", cmd);
        }
        // response.getData().toByteArray();
    }

    private Object[] getArgumentValues(byte[] data, HandlerMethod method) {
        Class<?>[] parameters = method.getParamsType();
        if (parameters.length == 0 && data.length > 0 || parameters.length > 0 && data.length <= 0) {
            throw new IllegalArgumentException("Not have response data");
        }
        // Default use the first param to parse the response data
        if (parameters.length > 0) {
            Class<?> parameter = parameters[0];

            return new Object[]{ProtoBufUtils.deSerialize(data, parameter)};
        }
        return null;
    }

}

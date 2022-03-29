package com.lzh.game.client.bootstrap;

import com.lzh.game.client.support.ExchangeProtocol;
import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.common.scoket.ActionMethodSupport;
import com.lzh.game.common.serialization.ProtoBufUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;

import java.util.Objects;

@Slf4j
public class ResponseDispatcher {

    private ActionMethodSupport<HandlerMethod> methodSupport;

    public ResponseDispatcher(ActionMethodSupport<HandlerMethod> methodSupport) {
        this.methodSupport = methodSupport;
    }

    public void doResponse(Channel channel, ExchangeProtocol.Response response) {
        //SerializationUtil.deSerialize()
        int cmd = response.getHead().getCmd();
        HandlerMethod method = methodSupport.getActionHandler(cmd);
        if (Objects.isNull(method)) {
            throw new IllegalArgumentException("Not register the " + cmd + " protocol !!");
        }

        try {
            Object[] params = getArgumentValues(response.getData().toByteArray(), method);
            method.doInvoke(params);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("{} response error", cmd);
        }
        // response.getData().toByteArray();
    }

    private Object[] getArgumentValues(byte[] data, HandlerMethod method) {
        MethodParameter[] parameters = method.getMethodParameters();
        if (parameters.length <= 0 && data.length > 0 || parameters.length > 0 && data.length <= 0) {
            throw new IllegalArgumentException("Not have response data");
        }
        // Default use the first param to parse the response data
        if (parameters.length > 0) {
            MethodParameter defaultValue = parameters[0];

            return new Object[]{ProtoBufUtils.deSerialize(data, defaultValue.getParameterType())};
        }
        return null;
    }

}

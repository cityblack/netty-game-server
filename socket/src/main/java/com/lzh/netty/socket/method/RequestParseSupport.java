package com.lzh.netty.socket.method;

import com.lzh.netty.socket.protocol.Request;
import com.lzh.netty.socket.util.JsonUtil;
import org.springframework.core.CollectionFactory;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class RequestParseSupport {

    private final Request request;
    private Map<String,Object> requestMap;

    public RequestParseSupport(Request request) {
        this.request = request;
        this.parseRequest(request);
    }

    private void parseRequest(Request request) {
        this.requestMap = JsonUtil.jsonToMap(request.data());
    }

    public Object getArgumentValue(MethodParameter parameter, DataBindHandler dataBindHandler) {
        String parameterName = parameter.getParameterName();
        Object argument = this.requestMap.remove(parameterName);

        if (argument == null) {
            return getEmptyArgument(parameter.getParameterType());
        }
        return dataBindHandler.conventData(argument + "",parameter);
    }

    private Object getEmptyArgument(Class<?> fieldType) {

        if (Boolean.class == fieldType || boolean.class == fieldType) {
            return Boolean.FALSE;
        } else if (fieldType.isArray()) {
            // Special handling of array property.
            return Array.newInstance(fieldType.getComponentType(), 0);
        }
        else if (Collection.class.isAssignableFrom(fieldType)) {
            return CollectionFactory.createCollection(fieldType, 0);
        }
        else if (Map.class.isAssignableFrom(fieldType)) {
            return CollectionFactory.createMap(fieldType, 0);
        }
        return null;
    }
}

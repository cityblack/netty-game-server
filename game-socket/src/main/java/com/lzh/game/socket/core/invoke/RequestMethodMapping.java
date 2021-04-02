package com.lzh.game.socket.core.invoke;

import com.lzh.game.common.bean.HandlerMethod;
import lombok.Data;

import java.util.Objects;

@Data
public class RequestMethodMapping {
    /**
     * cmd id
     */
    private int value;

    private int response;

    private HandlerMethod handlerMethod;

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RequestMethodMapping)) {
            return false;
        }
        if (((RequestMethodMapping) obj).getValue() == this.getValue()) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.value);
    }

    public boolean hasResponse() {
        return this.getResponse() != 0;
    }

    public RequestMethodMapping copy() {
        RequestMethodMapping requestMethodMapping = new RequestMethodMapping();
        requestMethodMapping.setValue(this.value);
        requestMethodMapping.setResponse(this.response);
        return requestMethodMapping;
    }
}

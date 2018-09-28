package com.lzh.netty.socket.dispatcher.protocol;

import lombok.Data;

import java.util.Objects;

@Data
public class ProtocolModel {
    /**
     * Protocol id
     */
    private int value;
    /**
     * Describe the protocol
     */
    private String desc;

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ProtocolModel)) {
            return false;
        }
        if (((ProtocolModel) obj).getValue() == this.getValue()) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.value);
    }
}

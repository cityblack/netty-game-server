package com.lzh.netty.framework.core.player;

import java.io.Serializable;

public class Player implements Serializable {

    private static final long serialVersionUID = 2728415253086770853L;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

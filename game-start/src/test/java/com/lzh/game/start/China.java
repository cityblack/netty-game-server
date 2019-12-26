package com.lzh.game.start;

import lombok.Data;

@Data
public class China extends User {

    private String first;

    public China(String first) {
        this.first = first;
    }

    public China() {
    }
}

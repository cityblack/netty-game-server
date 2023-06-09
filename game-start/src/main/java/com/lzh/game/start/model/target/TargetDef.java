package com.lzh.game.start.model.target;

import lombok.Data;

import java.util.Map;

@Data
public class TargetDef {

    private TargetType type;

    private Map<String, String> param;

    private int key;

    private int value;

    private int history;
}

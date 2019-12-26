package com.lzh.game.start.model.item.packet;

import lombok.Data;

import java.util.Map;

@Data
public class UseItemRequest {

    private long objectId;

    private int num;

    private Map<String, String> param;
}

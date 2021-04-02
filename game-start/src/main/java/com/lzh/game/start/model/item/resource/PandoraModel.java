package com.lzh.game.start.model.item.resource;

import lombok.Data;

@Data
public class PandoraModel {

    private PandoraModelInner[] item;

    private int weight;

    @Data
    public static class PandoraModelInner {

        private int itemId;

        private int num;
    }

}

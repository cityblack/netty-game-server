package com.lzh.game.start.model.item.model;

public enum ItemType {

    USE_ABLE {
        @Override
        public UseAbleItem create() {
            return new UseAbleItem();
        }
    },

    CONSUME {
        @Override
        public ConsumeItem create() {
            return new ConsumeItem();
        }
    },

    PANDORA {
        @Override
        public PandoraItem create() {
            return new PandoraItem();
        }
    }
    ;
    public abstract <T extends AbstractItem> T create();
}

package com.lzh.game.start.model.util.consume;

import com.lzh.game.start.model.util.consume.handle.AndConsume;
import com.lzh.game.start.model.util.consume.handle.ItemConsume;

/**
 * Consume type
 * use by config file. for example:
 *  {"type":"item", "value":"1001_1"} -> the config will find the {@link ItemConsume} class
 *  by the name of ITEM, and return {@link Consume}
 */
public enum ConsumeType {
    //用于数组配置的
    AND {
        @Override
        public AndConsume create() {
            return new AndConsume();
        }
    },
    // {type:"ITEM",value:"id_num"}
    ITEM {
        @Override
        public ItemConsume create() {
            return new ItemConsume();
        }
    },
    ;

    public abstract <T extends AbstractConsume> T create();
}

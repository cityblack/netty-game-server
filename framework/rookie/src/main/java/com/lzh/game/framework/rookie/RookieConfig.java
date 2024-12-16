package com.lzh.game.framework.rookie;

import lombok.Data;

/**
 * @author zehong.l
 * @since 2024-12-13 14:59
 **/
@Data
public class RookieConfig {

    /**
     * Whether write wrapper class info
     */
    private boolean writeClassWrapper;

    /**
     * compress collection serialize class info
     * If value == 0. Ignore compress class info
     * Compress class info when value == x and collection.size() >= x
     */
    private int compressCollectionValueSize;


    private boolean compressMapKeyValueClass;
}

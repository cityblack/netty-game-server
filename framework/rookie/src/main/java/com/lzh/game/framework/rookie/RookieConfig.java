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

    private int compressMapKeyValueClassSize;
}

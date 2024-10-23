package com.lzh.game.framework.rookie.utils;

import java.util.Objects;

/**
 * @author zehong.l
 * @since 2024-09-19 16:16
 **/
public class Constant {
    public static final byte NONE = 0b0;
    public static final byte HAS_VALUE = 0b1;
    public static final int INNER_TYPE_ID_MIN = 0;
    public static final int INNER_TYPE_ID_MAX = 100;

    public static byte getValueSign(Object value) {
        return Objects.isNull(value) ? NONE : HAS_VALUE;
    }
}

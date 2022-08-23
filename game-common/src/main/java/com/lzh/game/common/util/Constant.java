package com.lzh.game.common.util;

public class Constant {

    public static final byte REQUEST_SIGN = 0x00;
    public static final byte RESPONSE_SIGN = 0x01;
    public static final byte ONEWAY_SIGN = 0x02;

    public static final boolean IS_REQUEST_SIGN(byte sign) {
        return sign == REQUEST_SIGN || sign == ONEWAY_SIGN;
    }
}

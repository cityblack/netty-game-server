package com.lzh.game.socket;

public class Constant {

    public static final byte REQUEST_SIGN = 0x00;
    public static final byte RESPONSE_SIGN = 0x01;
    public static final byte ONEWAY_SIGN = 0x02;

    public static final boolean isRequest(byte type) {
        return type == REQUEST_SIGN || type == ONEWAY_SIGN;
    }

    public static final int HEAD_LEN = 4;
    // Request message type. min
    public static final int MESSAGE_TYPE_MIN = 2;

    public static final int REQUEST_ID_MIN = 4;

    public static final int HEAD_MIN_LEN = HEAD_LEN + MESSAGE_TYPE_MIN + REQUEST_ID_MIN;
}

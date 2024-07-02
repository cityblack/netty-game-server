package com.lzh.game.framework.socket.utils;

public class Constant {

    public static final byte REQUEST_SIGN = 0x00;
    public static final byte RESPONSE_SIGN = 0x01;
    public static final byte ONEWAY_SIGN = 0x02;

    public static boolean isRequest(byte type) {
        return type == REQUEST_SIGN || type == ONEWAY_SIGN;
    }

    public static final int DEFAULT_SERIAL_SIGN = 0;

    public static final int HEAD_LEN = 4;
    // Request message type. min
    public static final int MESSAGE_ID = 2;

    public static final int MESSAGE_TYPE = 1;

    public static final int REQUEST_ID_MIN = 4;

    public static final int HEAD_MIN_LEN = HEAD_LEN + MESSAGE_ID + MESSAGE_TYPE + REQUEST_ID_MIN;
}

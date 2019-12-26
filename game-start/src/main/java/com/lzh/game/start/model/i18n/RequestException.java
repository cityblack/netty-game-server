package com.lzh.game.start.model.i18n;

public class RequestException extends RuntimeException {

    private int i18n;

    public RequestException(int i18n) {
        super("error " + i18n);
        this.i18n = i18n;
    }

    public int getI18n() {
        return i18n;
    }
}

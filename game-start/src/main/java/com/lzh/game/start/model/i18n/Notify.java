package com.lzh.game.start.model.i18n;

import lombok.Data;

@Data
public class Notify {

    private int i18nId;

    public static Notify of(int i18nId) {
        Notify notify = new Notify();
        notify.i18nId = i18nId;
        return notify;
    }
}

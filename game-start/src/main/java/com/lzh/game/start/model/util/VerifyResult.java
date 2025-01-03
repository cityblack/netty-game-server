package com.lzh.game.start.model.util;

import com.lzh.game.start.model.i18n.I18n;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class VerifyResult {

    @Getter
    @Setter
    private boolean success = true;

    @Setter
    @Getter
    private int errorCode = I18n.SYS_ERROR;
    /**
     * 上下传递对象
     */
    @Getter
    private Map<Object, Object> verifyContext = new HashMap<>();

    @Getter
    @Setter
    private boolean isLastContext;

    public boolean isSuccess() {
        return success;
    }

    public void fail() {
        success = false;
    }

    public static VerifyResult of() {

        return new VerifyResult();
    }
}

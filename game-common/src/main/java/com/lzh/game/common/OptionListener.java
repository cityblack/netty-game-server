package com.lzh.game.common;

import java.util.EventListener;

public interface OptionListener<S> extends EventListener {

    void success(S s);

    void error(Throwable throwable);
}

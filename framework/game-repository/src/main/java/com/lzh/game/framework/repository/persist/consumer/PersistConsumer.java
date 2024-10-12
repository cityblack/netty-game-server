package com.lzh.game.framework.repository.persist.consumer;

import com.lzh.game.framework.repository.persist.Element;
import com.lzh.game.framework.repository.persist.PersistRepository;

public interface PersistConsumer {

    void onConsumer(Element element);

    PersistRepository repository();

    boolean serialize(Element element);
}

package com.lzh.game.framework.repository.db.persist;

import com.lzh.game.framework.repository.db.Element;
import com.lzh.game.framework.repository.db.PersistRepository;

public interface PersistConsumer {

    void onConsumer(Element element);

    PersistRepository repository();

    boolean serialize(Element element);
}

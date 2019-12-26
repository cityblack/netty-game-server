package com.lzh.game.repository.db.persist;

import com.lzh.game.repository.db.Element;
import com.lzh.game.repository.db.PersistRepository;

public interface PersistConsumer {

    void onConsumer(Element element);

    PersistRepository repository();

    boolean serialize(Element element);
}

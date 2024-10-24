package com.lzh.game.framework.socket.core.invoke.select.factory;

import com.lzh.game.framework.socket.core.bootstrap.client.AbstractClient;
import com.lzh.game.framework.socket.core.invoke.select.SessionSelect;
import com.lzh.game.framework.socket.core.invoke.select.SessionSelectContext;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionEvent;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomSessionSelectFactory implements SessionSelectFactory {

    @Override
    public SessionSelect<SessionSelectContext> createSessionSelect(AbstractClient<?> client) {
        return new Select(client);
    }

    static class Select implements SessionSelect<SessionSelectContext> {
        private final AtomicInteger COUNT = new AtomicInteger(0);

        private final AbstractClient<?> client;

        private final CopyOnWriteArrayList<Session> sessions;

        public Select(AbstractClient<?> client) {
            this.client = client;
            this.sessions = new CopyOnWriteArrayList<>();
            this.init();
        }

        private void init() {
            var manage = client.getContext().getSessionManage();
            manage.addListener(SessionEvent.CONNECT, (s, o) -> sessions.add(s));
            manage.addListener(SessionEvent.CLOSE, (s, o) -> sessions.remove(s));
        }

        @Override
        public Session selected(SessionSelectContext context) {
            if (sessions.isEmpty()) {
                return null;
            }
            int index = COUNT.incrementAndGet() % sessions.size();
            return sessions.get(index);
        }
    }
}

package com.lzh.game.framework.socket.core.protocol.message;

import com.lzh.game.framework.utils.ClassScannerUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zehong.l
 * @since 2024-08-22 10:44
 **/
public class MessageLoadHandler {

    public List<MessageDefine> load(String[] packageNames) {
        var list = new ArrayList<MessageDefine>();
        loadDefined(list, packageNames);
        return list;
    }


    private void loadDefined(List<MessageDefine> list, String[] packageNames) {
        ClassScannerUtils.scanPackage(packageNames, e -> e.isAnnotationPresent(Protocol.class), e -> {
            var defined = MessageManager.classToDefine(e);
            list.add(defined);
        });
    }
}

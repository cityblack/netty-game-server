package com.lzh.game.framework.socket.core.protocol.serial.impl.fury;

import com.lzh.game.framework.socket.proto.RequestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.fury.Fury;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author zehong.l
 * @since 2024-08-22 18:49
 **/
@SpringBootTest(classes = FurySerializeTest.class)
@Slf4j
class FurySerializeTest {

    private Fury fury = Fury.builder()
            .withRefTracking(true)
            .requireClassRegistration(true)
            .withAsyncCompilation(true).build();

    @Test
    void decode() {
        fury.register(RequestData.class, (short)10086);
        var request = new RequestData(1L, 30, "lzh", 0.1D, 174.3F);
        var bytes = fury.serialize(request);
        log.info("len: {}", bytes.length);
    }

    @Test
    void encode() {
    }
}
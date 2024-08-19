package com.lzh.game.framework.hotswap.agent;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zehong.l
 * @since 2024-08-15 18:18
 **/
public class HotSwapUtils {

    public static byte[] classToBytes(Collection<ClassFileInfo> files) throws IOException {
        var date = new ByteArrayOutputStream();
        try (var outputStream = new DataOutputStream(date)) {
            outputStream.writeInt(files.size());
            for (var info : files) {
                outputStream.writeUTF(info.getClasName());
                outputStream.writeInt(info.getData().length);
                outputStream.write(info.getData());
            }
        }
        return date.toByteArray();
    }

    public static Map<String, byte[]> byteToClassInfo(byte[] data) throws IOException {
        var map = new HashMap<String, byte[]>();
        try (var dis = new DataInputStream(new ByteArrayInputStream(data))) {
            int len = dis.readInt();
            for (int i = 0; i < len; i++) {
                var className = dis.readUTF();
                var classLen = dis.readInt();
                var bytes = new byte[classLen];
                dis.readFully(bytes);
                map.put(className, bytes);
            }
        }
        return map;
    }
}

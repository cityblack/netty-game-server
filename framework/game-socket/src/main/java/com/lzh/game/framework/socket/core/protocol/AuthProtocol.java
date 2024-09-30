package com.lzh.game.framework.socket.core.protocol;

import com.lzh.game.framework.common.utils.CrcUtils;
import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import com.lzh.game.framework.socket.utils.Constant;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;

/**
 * @author zehong.l
 * @since 2024-07-30 17:48
 **/
@Protocol(value = Constant.AUTH_PROTOCOL_ID)
@Data
public class AuthProtocol {

    private String value1;

    private int value2;

    public static AuthProtocol of(String slot) {
        var pro = new AuthProtocol();
        var encode = encode(slot);
        int c = CrcUtils.crc32(encode.getBytes(StandardCharsets.UTF_8));
        pro.value1 = encode;
        pro.value2 = c;
        return pro;
    }

    private static String encode(String slot) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(slot.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verify(String slot) {
        var encode = encode(slot);
        int value = CrcUtils.crc32(encode.getBytes(StandardCharsets.UTF_8));
        if (Objects.equals(encode, this.value1) && value == this.value2) {
            return true;
        }
        return false;
    }
}

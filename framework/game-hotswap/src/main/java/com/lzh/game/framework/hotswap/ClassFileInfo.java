package com.lzh.game.framework.hotswap;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author zehong.l
 * @since 2024-08-02 18:12
 **/
public class ClassFileInfo {

    private final String path;

    private final String clasName;

    private final byte[] data;

    private final String md5;

    private final long lastModifyTime;

    public ClassFileInfo(String path, String clasName, byte[] data, long lastModifyTime) {
        this.path = path;
        this.clasName = clasName;
        this.data = data;
        this.lastModifyTime = lastModifyTime;
        this.md5 = md5();
    }

    private String md5() {
        try {
            var md5 = MessageDigest.getInstance("MD5");
            md5.update(this.data);
            var bi = new BigInteger(1, md5.digest());
            return bi.toString(16).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public String getPath() {
        return path;
    }

    public String getClasName() {
        return clasName;
    }

    public byte[] getData() {
        return data;
    }

    public String getMd5() {
        return md5;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }
}

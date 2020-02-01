package com.ttsnake.common.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

public class CodecUtils {

    public static String md5Hex(String data) {
        return DigestUtils.md5Hex(data.getBytes());
    }

    public static String md5Hex(String data,String salt) {
        String tmp = salt + data;
        return DigestUtils.md5Hex(tmp.getBytes());
    }

    /*public static String shaHex(String data, String salt) {
        if (StringUtils.isBlank(salt)) {
            salt = data.hashCode() + "";
        }
        return DigestUtils.sha512Hex(salt + DigestUtils.sha512Hex(data));
    }*/

    public static String generateSalt(){
        return StringUtils.replace(UUID.randomUUID().toString(), "-", "");
    }

    public static String generateSerial(){
        return StringUtils.replace(UUID.randomUUID().toString(), "-", "");
    }
}

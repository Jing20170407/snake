package com.ttsnake.common.utils;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class NettyUtils {
    public static <T> T getAttr(Class<T> clazz,String name, Channel channel) {
        Attribute<T> attr = channel.attr(AttributeKey.valueOf(name));
        return attr.get();
    }


    public static void setAttr(String name,Object o, Channel channel) {
        Attribute<Object> attr = channel.attr(AttributeKey.valueOf(name));
        attr.set(o);
    }
}

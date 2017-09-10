package me.libme.fn.netty.msg;

import me.libme.fn.netty.json.JJSON;
import me.libme.fn.netty.util.JStringUtils;

/**
 * Created by J on 2017/9/7.
 */
public interface BodyEncoder<T> {

    byte[] encode(T content);


    public static class SimpleJSONEncoder implements BodyEncoder<Object> {
        @Override
        public byte[] encode(Object content) {
            return JStringUtils.utf8(JJSON.get().format(content));
        }
    }

}

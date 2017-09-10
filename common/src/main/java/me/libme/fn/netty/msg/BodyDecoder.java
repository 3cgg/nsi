package me.libme.fn.netty.msg;

import me.libme.fn.netty.json.JJSON;
import me.libme.fn.netty.util.JStringUtils;

/**
 * Created by J on 2017/9/7.
 */
public interface BodyDecoder<T> {

    T decode(byte[] content);


    public static class SimpleJSONDecoder<T> implements BodyDecoder<T> {

        private Class<T> clazz;

        public SimpleJSONDecoder(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public T decode(byte[] content) {
            String _content= JStringUtils.utf8(content);
            return JJSON.get().parse(_content,clazz);
        }

    }

}

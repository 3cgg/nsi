package me.libme.fn.netty.client.msg;

import me.libme.fn.netty.MessageMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by J on 2017/9/6.
 */
@MessageMeta
public class MsgHeader {



    public class Entry{

        private String name;

        private String value;

        @Override
        public String toString() {
            return name+"->"+value;
        }
    }

    private Map<String , Entry> entries=new HashMap<>();

    /**
     * add or replace the entry with the name key
     * @param name
     * @param value
     * @return
     */
    public MsgHeader addEntry(String name, String value){
        Entry entry=new Entry();
        entry.name=name;
        entry.value=value;
        entries.put(name,entry);
        return this;
    }


    public MsgHeader remove(String name){
        entries.remove(name);
        return this;
    }

    public String get(String name){
        Entry entry=entries.get(name);
        if(entry!=null){
            return entry.value;
        }
        return null;
    }

    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        for (Entry entry : entries.values()) {
            headers.put(entry.name, entry.value);
        }
        return Collections.unmodifiableMap(headers);
    }


    @Override
    public String toString() {
        return entries.values().toString();
    }
}

package me.libme.fn.netty.client.msg;

import me.libme.fn.netty.MessageBody;
import me.libme.fn.netty.json.JJSON;
import me.libme.fn.netty.msg.BodyEncoder;
import me.libme.fn.netty.util.JStringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by J on 2017/9/6.
 */
@MessageBody
public class FormMsgBody implements IMessageBody {

    private BodyEncoder bodyEncoder=new BodyEncoder.SimpleJSONEncoder();


    public void setBodyEncoder(BodyEncoder bodyEncoder) {
        this.bodyEncoder = bodyEncoder;
    }

    public class Entry{

        private String name;

        private Object value;

    }

    private Map<String , Entry> entries=new HashMap<>();


    /**
     * add or replace the entry with the name key
     * @param name
     * @param value
     * @return
     */
    public FormMsgBody addEntry(String name, String value){
        Entry entry=new Entry();
        entry.name=name;
        entry.value=value;
        entries.put(name,entry);
        return this;
    }

    /**
     * add or replace the entry with the name key
     * @param name
     * @param value
     * @return
     */
    public FormMsgBody addEntry(String name, Object value){
        Entry entry=new Entry();
        entry.name=name;
        entry.value=value;
        entries.put(name,entry);
        return this;
    }


    public FormMsgBody remove(String name){
        entries.remove(name);
        return this;
    }


    @Override
    public byte[] body() {
        String data="";
        for(Entry entry:entries.values()){
            try {
                data=data+"&"+entry.name+"="+ URLEncoder.encode(JJSON.get().format(entry.value),"utf-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return JStringUtils.utf8(data.substring(1));
    }
}

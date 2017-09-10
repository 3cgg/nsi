package me.libme.fn.netty.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by J on 2016/3/9.
 */
public class JUniqueUtils {


    public final static String SEQUECE = "yyyyMMddHHmmssSSS";

    public static String unique() {
        return UUID.randomUUID().toString();
    }

    /**
     * get time format of the format 'yyyyMMddHHmmssSSS'
     * @return
     */
    public static String sequence(){
        SimpleDateFormat oFormat = new SimpleDateFormat(SEQUECE);
        oFormat.setLenient(false);
        return oFormat.format(new Timestamp(new Date().getTime()));
    }

}

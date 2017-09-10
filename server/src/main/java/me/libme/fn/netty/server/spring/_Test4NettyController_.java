package me.libme.fn.netty.server.spring;

import me.libme.fn.netty.util.JUniqueUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by J on 2017/9/7.
 */
@RestController("/_test4netty_")
public class _Test4NettyController_ {

    @RequestMapping("/name")
    @ResponseBody
    public String name(String name) {
        return "----["+ JUniqueUtils.sequence()+"] already got name : " +name;
    }



}

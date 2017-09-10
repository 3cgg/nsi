package me.libme.fn.netty.server.spring;

import me.libme.fn.netty.async.ThreadConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by J on 2017/8/10.
 */
@ConfigurationProperties(prefix="cpp.netty.server.bsy.thread")
@Component
public class _ThreadConfig extends ThreadConfig {

}

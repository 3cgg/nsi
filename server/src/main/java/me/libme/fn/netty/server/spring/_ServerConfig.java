package me.libme.fn.netty.server.spring;

import me.libme.fn.netty.server.ServerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by J on 2017/9/7.
 */
@ConfigurationProperties(prefix="cpp.netty.server")
@Component
public class _ServerConfig extends ServerConfig implements EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment=environment;
    }

}

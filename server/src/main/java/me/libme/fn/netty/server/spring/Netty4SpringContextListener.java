package me.libme.fn.netty.server.spring;

import me.libme.fn.netty.json.JJSON;
import me.libme.fn.netty.server.SimpleHttpNioChannelServer;
import me.libme.fn.netty.server.SimpleRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by J on 2017/9/7.
 */
@Component
public class Netty4SpringContextListener implements ApplicationListener<ContextRefreshedEvent> , ApplicationContextAware{

    private static final Logger LOGGER= LoggerFactory.getLogger(Netty4SpringContextListener.class);

    private ApplicationContext applicationContext;

    @Autowired
    private _ServerConfig serverConfig;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        SimpleRequestHandler.setApplicationContext(applicationContext);

        // START SERVER
        SimpleHttpNioChannelServer channelServer =
                new SimpleHttpNioChannelServer(serverConfig);
        try {
            channelServer.start();
            LOGGER.info("inetHost ["+serverConfig.getHost()+"] listen on port : "+serverConfig.getPort()+", params : "+ JJSON.get().format(serverConfig));
        } catch (Exception e) {
            LOGGER.error( e.getMessage()+", params : "+ JJSON.get().format(serverConfig),e);
            try {
                channelServer.close();
            } catch (IOException e1) {
                LOGGER.error( e1.getMessage()+", params : "+ JJSON.get().format(serverConfig),e1);
                throw new RuntimeException(e);
            }
            throw new RuntimeException(e);
        }

    }


}

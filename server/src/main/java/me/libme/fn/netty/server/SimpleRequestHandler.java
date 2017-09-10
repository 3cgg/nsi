package me.libme.fn.netty.server;

import me.libme.fn.netty.async.Catch;
import me.libme.fn.netty.async.Task;
import me.libme.fn.netty.async.TaskExecutor;
import me.libme.fn.netty.util.JStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Created by J on 2017/9/7.
 */
public class SimpleRequestHandler implements RequestMappingHandler{

    private static final Logger LOGGER= LoggerFactory.getLogger(SimpleRequestHandler.class);

    private static ApplicationContext applicationContext;

    private static TaskExecutor taskExecutor;


    public static void setApplicationContext(ApplicationContext applicationContext) {
        SimpleRequestHandler.applicationContext = applicationContext;
        requestMappingHandler=applicationContext.getBean(RequestMappingHandler.class);
        taskExecutor=applicationContext.getBean(TaskExecutor.class);
    }

    private static RequestMappingHandler requestMappingHandler;

    @Override
    public void handle(final HttpRequest httpRequest) {
        taskExecutor.promise(new Task() {
            @Override
            public void run() {
                requestMappingHandler.handle(httpRequest);
            }
        }).cat(new Catch() {
            @Override
            public void cat(Throwable error) {
                LOGGER.error(error.getMessage(),error);
                httpRequest.getHttpResponse().write(JStringUtils.utf8("server error."));
            }
        });

    }


}

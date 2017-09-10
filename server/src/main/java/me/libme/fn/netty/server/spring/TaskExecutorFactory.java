package me.libme.fn.netty.server.spring;

import me.libme.fn.netty.async.SimpleThreadPoolExecutorFactory;
import me.libme.fn.netty.async.TaskExecutor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by J on 2017/8/10.
 */
@Component
public class TaskExecutorFactory implements FactoryBean<TaskExecutor> {


    @Autowired
    private _ThreadConfig cfg;

    @Override
    public TaskExecutor getObject() throws Exception {

        SimpleThreadPoolExecutorFactory simpleThreadPoolExecutorFactory=
                new SimpleThreadPoolExecutorFactory(cfg);
        return new TaskExecutor(simpleThreadPoolExecutorFactory);
    }

    @Override
    public Class<?> getObjectType() {
        return TaskExecutor.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}

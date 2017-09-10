package me.libme.fn.netty.client;

import java.util.EventListener;

public interface GenericPromiseListener<F extends CallPromise<?>> extends EventListener {

    /**
     * Invoked when the operation associated with the {@link CallPromise} has been completed.
     *
     * @param future  the source {@link CallPromise} which called this callback
     */
    void operationComplete(F callPromise) throws Exception;
}

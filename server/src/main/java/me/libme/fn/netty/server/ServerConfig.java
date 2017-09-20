package me.libme.fn.netty.server;

import me.libme.fn.netty.JModel;

/**
 * Created by J on 2017/9/7.
 */
public class ServerConfig implements JModel{

    private String host;

    private int port;

    private int loopThread;

    private int workerThread;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getLoopThread() {
        return loopThread;
    }

    public void setLoopThread(int loopThread) {
        this.loopThread = loopThread;
    }

    public int getWorkerThread() {
        return workerThread;
    }

    public void setWorkerThread(int workerThread) {
        this.workerThread = workerThread;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}

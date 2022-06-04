package com.kisman.cc.viaforge;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kisman.cc.Kisman;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.kisman.cc.viaforge.loader.BackwardsLoader;
import com.kisman.cc.viaforge.platform.*;
import com.kisman.cc.viaforge.util.JLoggerToLog4j;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoop;

import java.io.File;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class ViaForge {

    public final static int SHARED_VERSION = 340;

    private static final ViaForge instance = new ViaForge();
    private final Logger jLogger = new JLoggerToLog4j(Kisman.LOGGER);
    private final CompletableFuture<Void> initFuture = new CompletableFuture<>();
    private ExecutorService asyncExecutor;
    private EventLoop eventLoop;
    private File file;
    private int version;
    private String lastServer;

    public static ViaForge getInstance() {
        return instance;
    }

    public void start() {
        ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ViaForge-%d").build();
        asyncExecutor = Executors.newFixedThreadPool(8, factory);

        eventLoop = new DefaultEventLoopGroup(1, factory).next();
        eventLoop.submit(initFuture::join);

        setVersion(SHARED_VERSION);
        this.file = new File("ViaForge");
        if (this.file.mkdir()) this.getjLogger().info("Creating ViaForge Folder");

        Via.init(ViaManagerImpl.builder().injector(new Injector()).loader(new ProviderLoader()).platform(new Platform(file)).build());

        MappingDataLoader.enableMappingsCache();
        ((ViaManagerImpl) Via.getManager()).init();

        new BackwardsLoader(file);

        initFuture.complete(null);
    }

    public Logger getjLogger() {
        return jLogger;
    }

    public CompletableFuture<Void> getInitFuture() {
        return initFuture;
    }

    public ExecutorService getAsyncExecutor() {
        return asyncExecutor;
    }

    public EventLoop getEventLoop() {
        return eventLoop;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getLastServer() {
        return lastServer;
    }

    public void setLastServer(String lastServer) {
        this.lastServer = lastServer;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}

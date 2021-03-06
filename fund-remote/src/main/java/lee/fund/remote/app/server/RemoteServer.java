package lee.fund.remote.app.server;

import com.alibaba.fastjson.JSON;
import lee.fund.remote.Server;
import lee.fund.remote.app.client.RemoteClient;
import lee.fund.remote.container.ServiceContainer;
import lee.fund.remote.netty.server.NettyServer;
import lee.fund.remote.netty.server.ServerConfig;
import lee.fund.remote.registry.JetcdRegistry;
import lee.fund.util.jetcd.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/28 16:47
 * Desc:
 */
public abstract class RemoteServer implements Server {
    private static final Logger logger = LoggerFactory.getLogger(RemoteServer.class);
    private NettyServer server;
    private ServiceContainer serviceContainer;
    private ServerConfiguration conf;

    public RemoteServer(ServerConfiguration conf) {
        this.conf = conf;
        this.server = new NettyServer(new ServerConfig(conf));
        this.serviceContainer = new ServiceContainer();
        this.appExposeService();
    }

    @Override
    public void start() {
        this.server.start();
        this.register();
    }

    @Override
    public void shutdown() {
        this.server.shutdown();
    }

    @Override
    public void exposeService(Class<?> clazz, Object instance) {
        if (RemoteClient.isProxy(instance)) {
            throw new RuntimeException(String.format("can't register a proxy object as service [%s], this will cause dead circulation", clazz.getName()));
        }
        this.serviceContainer.storeService(clazz, instance);
        this.server.setServiceContainer(this.serviceContainer);
    }

    @Override
    public void register() {
        //TODO 测试一下注册
        if (conf.isRpcRegisterEnabled()) {
            JetcdRegistry.getInstance().register(() -> this.getProvider(conf));
//            Provider provider = this.getProvider(conf);
//            String value = JSON.toJSONString(provider);
//            try {
//                JetcdRegistry.getInstance().register(() -> provider);
//                logger.info("server[{}] register success: {}", provider.getName(), value);
//            } catch (Exception e) {
//                logger.info(String.format("server[%s] register failed", provider.getName()), e);
//            }
        }
    }

    @Override
    public Object getData(String key) {
        switch (key) {
            case "clients.active":
                return this.server.getServerHandler().getChannelGroup().size();
            case "clients.maxConnections":
                return this.server.getServerConfig().getMaxConnections();
            case "threads.active":
                return this.server.getThreadPool().getActiveCount();
            case "threads.max":
                return this.server.getServerConfig().getMaxThreads();
            default:
                return null;
        }
    }

    public Provider getProvider(ServerConfiguration conf) {
        Provider provider = new Provider();
        provider.setName(conf.getName());
        provider.setAddress(String.format("%s:%s", conf.getRegisterIp(), conf.getPort()));
        provider.setDesc(conf.getDesc());
//        provider.setClients((int) getData("clients.active"));  //TODO 调方法取，不用保到etcd
        return provider;
    }

    protected void appExposeService() {
        //TODO 初始化暴露些系统服务
    }
}

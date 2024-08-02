package gtanks.main.netty;

import gtanks.logger.Logger;
import gtanks.system.destroy.Destroyable;
import gtanks.test.osgi.OSGi;
import gtanks.test.server.configuration.entitys.NettyConfiguratorEntity;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;

public class NettyService implements Destroyable {
   private static final NettyService instance = new NettyService();
   public int port;
   private ServerBootstrap bootstrap;

   private NettyService() {
      this.initParams();
      ExecutorService bossExec = new OrderedMemoryAwareThreadPoolExecutor(1, 400000000L, 2000000000L, 60L, TimeUnit.SECONDS);
      ExecutorService ioExec = new OrderedMemoryAwareThreadPoolExecutor(4, 400000000L, 2000000000L, 60L, TimeUnit.SECONDS);
      ChannelFactory factory = new NioServerSocketChannelFactory(bossExec, ioExec, 4);
      this.bootstrap = new ServerBootstrap(factory);
      this.bootstrap.setPipelineFactory(new NettyPipelineFactory());
      this.bootstrap.setOption("child.tcpNoDelay", true);
      this.bootstrap.setOption("child.keepAlive", true);
   }

   public void init() {
      this.bootstrap.bind(new InetSocketAddress(this.port));
      Logger.log("[Netty] Server run on port: " + this.port);
   }

   public void destroy() {
      this.bootstrap.releaseExternalResources();
      this.bootstrap.shutdown();
   }

   public static NettyService inject() {
      return instance;
   }

   private void initParams() {
      this.port = ((NettyConfiguratorEntity)OSGi.getModelByInterface(NettyConfiguratorEntity.class)).getPort();
   }
}

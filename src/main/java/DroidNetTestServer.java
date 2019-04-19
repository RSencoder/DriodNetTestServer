import handler.ChannelInitializerImpl;
import handler.ServerChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * 抓包测试服务器，进行简单的HTTP echo响应
 */
public class DroidNetTestServer {
    private final int port;  //服务器绑定端口
    private final InetAddress serverAddr;  //服务器绑定地址

    public DroidNetTestServer(InetAddress addr, int port) {
        this.serverAddr = addr;
        this.port = port;
    }

    public static void main(String[] args) throws Exception{
        InetAddress address = InetAddress.getByName("10.5.71.223");
        new DroidNetTestServer(address,8123).start();
    }

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))  //自动绑定本机IP地址，可以不用手动设置serverAddr
                    .handler(new ServerChannelHandler())
                    .childHandler(new ChannelInitializerImpl());
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind().sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }
}

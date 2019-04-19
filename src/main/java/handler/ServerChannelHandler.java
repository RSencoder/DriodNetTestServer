package handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;

public class ServerChannelHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress localAddr = (InetSocketAddress) ctx.channel().localAddress();
        System.out.println("Server bind:" + localAddr.getAddress() + " port:" + localAddr.getPort());
    }
}

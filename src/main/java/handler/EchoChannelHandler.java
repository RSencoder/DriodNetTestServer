package handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 回送客户端请求HTTP报文中的消息
 */
public class EchoChannelHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();
        InetSocketAddress clientAddress = (InetSocketAddress) ch.remoteAddress();
        System.out.println("Client connected from:" + clientAddress.getAddress() + ":"
                + clientAddress.getPort());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;
        CompositeByteBuf requestBuf = (CompositeByteBuf) request.content();

        //解析客户端POST请求
        byte[] requestContentBytes = new byte[requestBuf.readableBytes()];
        requestBuf.readBytes(requestContentBytes);
        String requestContentStr = new String(requestContentBytes);
        System.out.println("Received:" + requestContentStr);
        Pattern pattern = Pattern.compile("requestContent=(.*)");
        Matcher matcher = pattern.matcher(requestContentStr);
        String requestContentValue = "";
        if (matcher.find()) {
            requestContentValue = matcher.group(1);
        }


        //将客户端请求报文中的消息加上"echo:"回送出去
        String replyMsg = "echo:" + requestContentValue;
        Charset utf8 = Charset.forName("UTF-8");
        ByteBuf replyContent = Unpooled.copiedBuffer(replyMsg, utf8);
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, replyMsg.length());
        httpResponse.content().writeBytes(replyContent);
        ctx.writeAndFlush(httpResponse);
        System.out.println("Replyed:" + replyMsg);

        //释放已经消费了的消息
        ReferenceCountUtil.release(msg);
    }
}

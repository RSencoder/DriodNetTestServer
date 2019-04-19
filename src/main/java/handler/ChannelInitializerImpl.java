package handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class ChannelInitializerImpl extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //HTTP服务器编解码
        pipeline.addLast("codec", new HttpServerCodec());
        //HTTP消息聚合
        pipeline.addLast("aggregator", new HttpObjectAggregator(512 * 1024));
        //HTTP测试服务器逻辑
        pipeline.addLast("echoLogic", new EchoChannelHandler());
    }
}

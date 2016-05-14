package com.angkorteam.mbaas.sdk.android.library.netty;

import android.content.Context;
import android.content.SharedPreferences;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Created by socheat on 5/8/16.
 */
public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    private final SharedPreferences preferences;
    private final Context context;

    public ClientInitializer(Context context, SharedPreferences preferences) {
        this.context = context;
        this.preferences = preferences;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());
        pipeline.addLast("handler", new ClientHandler(this.context, this.preferences));
    }

}

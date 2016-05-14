package com.angkorteam.mbaas.sdk.android.library.netty;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.angkorteam.mbaas.sdk.android.library.MBaaS;
import com.angkorteam.mbaas.sdk.android.library.SocketBroadcastReceiver;
import com.angkorteam.mbaas.sdk.android.library.MBaaSIntentService;
import com.angkorteam.mbaas.sdk.android.library.command.Authenticate;
import com.angkorteam.mbaas.sdk.android.library.command.MessageNotification;
import com.google.gson.Gson;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by socheat on 5/9/16.
 */
public class ClientHandler extends SimpleChannelInboundHandler<String> {

    public static final char SEPARATOR = ' ';

    public static final String COMMAND_MESSAGE_PRIVATE = "MESSAGE_PRIVATE";
    public static final String COMMAND_MESSAGE_GROUP = "MESSAGE_GROUP";
    public static final String COMMAND_MESSAGE_NOTIFICATION = "MESSAGE_NOTIFICATION";

    public static final String COMMAND_AUTHENTICATE = "AUTHENTICATE";

    public static final String COMMAND_GROUP_INITIATE = "GROUP_INITIATE";
    public static final String COMMAND_GROUP_INVITE = "GROUP_INVITE";
    public static final String COMMAND_GROUP_JOIN = "GROUP_JOIN";
    public static final String COMMAND_GROUP_LEAVE = "GROUP_LEAVE";

    public static final String COMMAND_FRIEND_REQUEST = "FRIEND_REQUEST";
    public static final String COMMAND_FRIEND_APPROVE = "FRIEND_APPROVE";
    public static final String COMMAND_FRIEND_REMOVE = "FRIEND_REMOVE";
    public static final String COMMAND_FRIEND_REJECT = "FRIEND_REJECT";
    public static final String COMMAND_FRIEND_BLOCK = "FRIEND_BLOCK";

    public static final String COMMAND_OKAY = "OK";
    public static final String COMMAND_ERROR = "ERROR";

    private final SharedPreferences preferences;

    private final LocalBroadcastManager manager;

    private final Gson gson;

    public ClientHandler(Context context, SharedPreferences preferences) {
        this.preferences = preferences;
        this.manager = LocalBroadcastManager.getInstance(context);
        this.gson = MBaaS.getInstance().getClient().getGson();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext context) throws Exception {
        Log.i("MBaaS", "handlerAdded");
    }

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        Log.i("MBaaS", "channelActive");
        String accessToken = this.preferences.getString(MBaaSIntentService.ACCESS_TOKEN, "");
        Authenticate authenticate = new Authenticate();
        authenticate.setAccessToken(accessToken);
        context.writeAndFlush(COMMAND_AUTHENTICATE + SEPARATOR + this.gson.toJson(authenticate));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, String msg) throws Exception {
        StringBuffer buffer = new StringBuffer();
        int index = 0;
        while (index < msg.length()) {
            Character character = msg.charAt(index);
            index++;
            if (character == ' ') {
                break;
            } else {
                buffer.append(character);
            }
        }
        String command = buffer.toString();
        String json = msg.substring(index);
        if (COMMAND_MESSAGE_NOTIFICATION.equals(command)) {
            MessageNotification messageNotification = this.gson.fromJson(json, MessageNotification.class);
            Intent intent = new Intent(SocketBroadcastReceiver.class.getName());
            intent.putExtra(SocketBroadcastReceiver.USER_ID, messageNotification.getUserId());
            intent.putExtra(SocketBroadcastReceiver.MESSAGE, messageNotification.getMessage());
            manager.sendBroadcast(intent);
        }
        Log.i("MBaaS", msg);
    }
}

package ru.andreyszdlv;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerHandler extends SimpleChannelInboundHandler<String> {

    private static final List<Channel> channels = new ArrayList<>();

    private static final Map<String, String> users = new HashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        String[] parts = msg.split(" ");
        String command = parts[0];

        switch (command) {
            case "login":
                handleLogin(ctx, parts);
                break;
            default:
                String name = users.get(ctx.channel().remoteAddress().toString());
                channels.forEach(channel -> channel.writeAndFlush(String.format("%s: %s\n", name, msg)));
        }
    }

    private void handleLogin(ChannelHandlerContext ctx, String[] parts) {
        if (parts.length < 2) {
            ctx.writeAndFlush("Ошибка: укажите имя пользователя. Пример: login -u=User123\n");
            return;
        }
        String username = parts[1].split("=")[1];
        users.put(ctx.channel().remoteAddress().toString(), username);
        ctx.writeAndFlush("Пользователь " + username + " вошел в систему!\n");
    }
}

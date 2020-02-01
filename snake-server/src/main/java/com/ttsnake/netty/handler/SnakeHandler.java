package com.ttsnake.netty.handler;

import com.ttsnake.common.SnakeProto;
import com.ttsnake.handler.GameHandler;
import com.ttsnake.handler.UserHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SnakeHandler extends SimpleChannelInboundHandler<SnakeProto.SnakeMessage> {

    private UserHandler userHandler;
    private GameHandler gameHandler;

    public SnakeHandler(UserHandler userHandler, GameHandler gameHandler) {
        this.userHandler = userHandler;
        this.gameHandler = gameHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info(ctx.channel().remoteAddress().toString() + " connected!");
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SnakeProto.SnakeMessage msg) throws Exception {

        if (msg.getDataType() == SnakeProto.SnakeMessage.DataType.LoginRequest) {
            SnakeProto.LoginRequest loginRequest = msg.getLoginRequest();
            userHandler.login(loginRequest.getUsername(), loginRequest.getPassword(), ctx.channel());
        } else if (msg.getDataType() == SnakeProto.SnakeMessage.DataType.GameRequest) {
            SnakeProto.GameRequest gameRequest = msg.getGameRequest();
            gameHandler.startGame(gameRequest, ctx);
        } else if (msg.getDataType() == SnakeProto.SnakeMessage.DataType.Single){
            SnakeProto.Single single = msg.getSingle();
            String type = single.getType();
            if ("command.direction".equals(type)) {
                gameHandler.commandDirection(ctx, new Integer(single.getJson()));
            }
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        userHandler.logout(ctx.channel());
        ctx.channel().close();
        log.info("close connect");
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.toString());
        //super.exceptionCaught(ctx, cause);
    }
}

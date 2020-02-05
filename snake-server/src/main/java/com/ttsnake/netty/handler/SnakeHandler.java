package com.ttsnake.netty.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ttsnake.common.SnakeProto;
import com.ttsnake.common.utils.JsonUtils;
import com.ttsnake.common.utils.ProtoUtils;
import com.ttsnake.common.vo.ChannelVO;
import com.ttsnake.handler.GameHandler;
import com.ttsnake.handler.UserHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashSet;

@Slf4j
public class SnakeHandler extends SimpleChannelInboundHandler<SnakeProto.SnakeMessage> {
    private HashSet<Channel> channelSet;

    private UserHandler userHandler;
    private GameHandler gameHandler;

    public SnakeHandler(UserHandler userHandler, GameHandler gameHandler,HashSet channelSet) {
        this.userHandler = userHandler;
        this.gameHandler = gameHandler;
        this.channelSet = channelSet;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        channelSet.add(ctx.channel());
        log.info(ctx.channel().remoteAddress().toString()+" registered!");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info(ctx.channel().remoteAddress().toString()+" unregistered!");
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress address = ctx.channel().remoteAddress();
        if (address != null) {
            log.info(address.toString() + " connected!");
        } else {
            log.info("channel active sever channel");
        }

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
        } else if (msg.getDataType() == SnakeProto.SnakeMessage.DataType.Single) {
            SnakeProto.Single single = msg.getSingle();
            String type = single.getType();
            if ("command.direction".equals(type)) {
                gameHandler.commandDirection(ctx, new Integer(single.getJson()));
            } else if ("channelSet".equals(type)) {
                ArrayList<ChannelVO> list = new ArrayList<>();
                channelSet.forEach(channel -> {
                    NioSocketChannel socketChannel = (NioSocketChannel) channel;
                    ChannelVO channelVO = new ChannelVO();

                    channelVO.setActive(socketChannel.isActive());
                    channelVO.setInputShutdown(socketChannel.isInputShutdown());
                    channelVO.setOpen(socketChannel.isOpen());
                    channelVO.setOutputShutdown(socketChannel.isOutputShutdown());
                    channelVO.setRegistered(socketChannel.isRegistered());
                    channelVO.setWritable(socketChannel.isWritable());
                    channelVO.setShutdown(socketChannel.isShutdown());

                    list.add(channelVO);
                });

                ctx.channel().writeAndFlush(ProtoUtils.getSingle("channelSet", JsonUtils.serialize(list)));
            }
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        Channel channel = ctx.channel();
        SocketAddress address = channel.remoteAddress();

        if (address != null) {
            userHandler.logout(channel);
            log.info(address.toString() + " disconnect!");
        } else {
            log.info("channel inactive server channel");
        }

        channel.close();
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof InvalidProtocolBufferException) {
            log.info(cause.toString());
        } else {
            log.error("exceptionCaught", cause);
        }

        //super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        channel.close();
    }
}

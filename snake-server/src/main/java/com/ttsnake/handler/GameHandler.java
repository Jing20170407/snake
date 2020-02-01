package com.ttsnake.handler;

import com.ttsnake.common.SnakeProto;
import com.ttsnake.common.pojo.User;
import com.ttsnake.common.utils.NettyUtils;
import com.ttsnake.common.utils.ProtoUtils;
import com.ttsnake.game.ClassicPlayGame;
import com.ttsnake.game.PlayGameManager;
import com.ttsnake.service.GameService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
public class GameHandler {
    @Autowired
    private GameService gameService;

    @Autowired
    private PlayGameManager manager;

    public void startGame(SnakeProto.GameRequest gameRequest, ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        //验证登录信息
        String sequence = gameRequest.getSequence();
        if (StringUtils.isBlank(sequence)) {
            channel.writeAndFlush(ProtoUtils.getGameResponce("401", "请登录"));
        }

        String localsequence = NettyUtils.getAttr(String.class,"sequence", channel);
        if (StringUtils.isBlank(localsequence)) {
            channel.writeAndFlush(ProtoUtils.getGameResponce("401", "请登录"));
        }

        if (!sequence.equals(localsequence)) {
            channel.writeAndFlush(ProtoUtils.getGameResponce("401", "请登录"));
        }

        //激活游戏
        User user = NettyUtils.getAttr(User.class, "user", ctx.channel());
        boolean b = manager.containsKey(user.getId());
        if (b) {
            ClassicPlayGame game = manager.get(user.getId());
            game.setChannel(channel);
            game.stopGameLogout();
            game.setGameFrame();
            channel.writeAndFlush(ProtoUtils.getGameResponce("200", "游戏退出重连"));
            return;
        }

        //启动游戏
        try {
            SnakeProto.SnakeMessage message = manager.startGame(ctx,gameRequest.getMode());
            channel.writeAndFlush(message);
        } catch (Exception e) {
            channel.writeAndFlush(ProtoUtils.getGameResponce("500", e.toString()));
            e.printStackTrace();
        }
    }

    public void commandDirection(ChannelHandlerContext ctx, Integer direction) {
        User user = NettyUtils.getAttr(User.class,"user", ctx.channel());
        if (manager.containsKey(user.getId())) {
            ClassicPlayGame game = manager.get(user.getId());
            //获取命令队列
            LinkedList<Integer> queue = game.getCommandQueue();

            //过滤条件，与队列中的比较
            if (queue.size() != 0) {
                Integer last = queue.getLast();//队列中最后的标记
                if (last.intValue() == direction.intValue()) {
                    return;
                }

                if ((last + direction) == 1) {
                    return;
                }

                if ((last + direction) == 5) {
                    return;
                }

                queue.addLast(direction);
                return;
            }


            //过滤条件，与对象中的比较
            Integer old = game.getSnake().getDirection();//对象中的标记
            if (old.intValue() == direction.intValue()) {
                return;
            }

            if ((old + direction) == 1) {
                return;
            }

            if ((old + direction) == 5) {
                return;
            }
            queue.addLast(direction);
        }
    }
}

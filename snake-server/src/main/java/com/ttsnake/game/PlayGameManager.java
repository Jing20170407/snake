package com.ttsnake.game;

import com.ttsnake.common.SnakeProto;
import com.ttsnake.common.utils.ProtoUtils;
import com.ttsnake.service.GameService;
import com.ttsnake.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Getter
@Slf4j
public class PlayGameManager {

    @Autowired
    private UserService userService;

    @Autowired
    private GameService gameService;

    private HashMap<Long, ClassicPlayGame> map = new HashMap<>();


    public SnakeProto.SnakeMessage startGame(ChannelHandlerContext ctx, int mode) {
        if (mode == 0) {
            ClassicPlayGame game = new ClassicPlayGame(this, ctx.channel(), ctx.executor());
            put(game.getUser().getId(), game);
            return ProtoUtils.getGameResponce("200", "ok");
        }

        return ProtoUtils.getGameResponce("400", "启动失败");
    }

    public void remove(Long id) {
        map.remove(id);
    }

    public void put(Long id, ClassicPlayGame game) {
        ClassicPlayGame playGame = map.put(id, game);
        //处理旧对象
        if (playGame != null) {
            playGame.stopGameLogout();
            playGame.stopGameFrame();
            playGame.stopGameRunner();
            log.warn(playGame+":坏对象，该对象已脱离管理器");
        }
    }

    public ClassicPlayGame get(Long id) {
        return map.get(id);
    }

    public boolean containsKey(Long id) {
       return map.containsKey(id);
    }
}

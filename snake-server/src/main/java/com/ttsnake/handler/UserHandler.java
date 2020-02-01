package com.ttsnake.handler;

import com.ttsnake.common.SnakeProto;
import com.ttsnake.common.pojo.Login;
import com.ttsnake.common.pojo.User;
import com.ttsnake.common.utils.JsonUtils;
import com.ttsnake.common.utils.NettyUtils;
import com.ttsnake.common.utils.ProtoUtils;
import com.ttsnake.game.ClassicPlayGame;
import com.ttsnake.game.PlayGameManager;
import com.ttsnake.service.LoginService;
import com.ttsnake.service.UserService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class UserHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private LoginService loginService;


    @Autowired
    private PlayGameManager manager;

    public void login(String username, String password, Channel channel) {
        SnakeProto.SnakeMessage message = userService.login(username, password, channel);
        //返回登录状态
        channel.writeAndFlush(message);

        //返回用户信息
        String code = message.getLoginResponse().getCode();
        if ("200".equals(code)) {
            User user = NettyUtils.getAttr(User.class,"user", channel);
            if (user != null) {
                log.debug("登陆后返回用户信息: "+user.toString());
                channel.writeAndFlush(ProtoUtils.getSingle("user", JsonUtils.serialize(user)));
            }
        }
    }

    public void logout(Channel channel) {
        //更新登录表
        Login login = NettyUtils.getAttr(Login.class,"login", channel);
        if (login == null) {
            return;
        }

        login.setState(1);
        login.setEnd_time(LocalDateTime.now());
        loginService.updateLogin(login);
        //查看游戏有无完成
        User user = NettyUtils.getAttr(User.class,"user", channel);
        if (manager.containsKey(user.getId())) {
            ClassicPlayGame game = manager.get(user.getId());
            game.stopGameFrame();
            game.setGameLogout();
        }
    }
}

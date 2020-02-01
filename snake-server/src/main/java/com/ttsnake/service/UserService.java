package com.ttsnake.service;

import com.ttsnake.common.SnakeProto;
import com.ttsnake.common.pojo.Login;
import com.ttsnake.common.pojo.User;
import com.ttsnake.common.utils.CodecUtils;
import com.ttsnake.common.utils.NettyUtils;
import com.ttsnake.common.utils.ProtoUtils;
import com.ttsnake.mapper.UserMapper;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class UserService {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginService loginService;

    public SnakeProto.SnakeMessage login(String username, String password, Channel channel) {
        //查询用户
        User user = new User();
        user.setUsername(username);
        User one = userMapper.selectOne(user);
        if (one == null) {
            return ProtoUtils.getLoginResponce("400", "", "用户名密码错误");
        }

        //验证密码
        String pass = CodecUtils.md5Hex(password, one.getSalt());
        if (!pass.equals(one.getPassword())) {
            return ProtoUtils.getLoginResponce("400", "", "用户名密码错误");
        }

        //用户是否已经登陆
        boolean b = loginService.checkLogin(one.getId(), 0);
        if (!b) {
            return ProtoUtils.getLoginResponce("400", "", "该用户已登陆");
        }

        //验证成功
        NettyUtils.setAttr("user",one,channel);

        Login login = new Login();
        login.setUser_id(one.getId());
        InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
        login.setClient_ip(address.getAddress().getHostAddress());
        InetSocketAddress localAddress = (InetSocketAddress) channel.localAddress();
        login.setServer_ip(localAddress.getAddress().getHostAddress());
        LocalDateTime time = LocalDateTime.now();
        login.setStart_time(time);
        login.setState(0);
        loginService.insertLogin(login);
        NettyUtils.setAttr("login", login, channel);


        String serial = CodecUtils.generateSerial();
        NettyUtils.setAttr("sequence", serial, channel);

        return ProtoUtils.getLoginResponce("200", serial, "登录成功");
    }

    public void register(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setSalt(CodecUtils.generateSalt());
        user.setPassword(CodecUtils.md5Hex(CodecUtils.md5Hex(password), user.getSalt()));
        userMapper.insertSelective(user);
    }

    public void updateUser(User user) {
        userMapper.updateByPrimaryKeySelective(user);
    }


}

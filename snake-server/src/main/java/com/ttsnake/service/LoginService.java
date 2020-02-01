package com.ttsnake.service;

import com.ttsnake.common.pojo.Login;
import com.ttsnake.mapper.LoginMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.NotLinkException;

@Service
@Slf4j
public class LoginService {

    @Autowired
    private LoginMapper loginMapper;

    public void insertLogin(Login login) {
        loginMapper.insertSelective(login);
    }

    public void updateLogin(Login login) {
        loginMapper.updateByPrimaryKeySelective(login);
    }

    public boolean checkLogin(Long userId, Integer state) {
        Login login = new Login();
        login.setUser_id(userId);
        login.setState(state);

        try {
            Login one = loginMapper.selectOne(login);

            if (one == null) {
                return true;//true为可用
            }

        } catch (Exception e) {
            log.error("登陆表异常",e);
        }

        return false;
    }
}

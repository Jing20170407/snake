package com.ttsnake.test;

import com.ttsnake.common.pojo.Snake;
import com.ttsnake.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Example;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestService {

    @Autowired
    private UserService userService;

    @Test
    public void test1() {
        /*try {
            userService.register("guest2","123456");
            userService.register("guest3","123456");
            userService.register("guest4","123456");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}

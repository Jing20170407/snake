package com.ttsnake.service;

import com.ttsnake.common.pojo.Game;
import com.ttsnake.mapper.GameMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class GameService {

    @Autowired
    private GameMapper gameMapper;

    public int insertGame(Game game) {
        return gameMapper.insertSelective(game);
    }

    public int updateGame(Game game) {
        return gameMapper.updateByPrimaryKeySelective(game);
    }

}

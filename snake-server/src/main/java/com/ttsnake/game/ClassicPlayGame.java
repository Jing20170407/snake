package com.ttsnake.game;

import com.ttsnake.common.pojo.*;
import com.ttsnake.common.utils.JsonUtils;
import com.ttsnake.common.utils.NettyUtils;
import com.ttsnake.common.utils.ProtoUtils;
import io.netty.channel.Channel;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.ScheduledFuture;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@Slf4j
public class ClassicPlayGame {
    private Integer commandTime = 0;

    private Game game;
    private Window window;
    private Snake snake;
    private Food food;
    private ScheduledFuture gameFrame;
    private ScheduledFuture gameRunner;
    private ScheduledFuture gameLogout;

    private User user;
    private Channel channel;
    private EventExecutor executor;
    private PlayGameManager manager;

    private LinkedList<Integer> commandQueue;


    public ClassicPlayGame(PlayGameManager manager, Channel channel, EventExecutor executor) {
        this.manager = manager;
        this.channel = channel;
        this.executor = executor;

        window = new Window(70, 50, 10, Color.BLACK);
        snake = new Snake(window);
        food = new Food(window);
        snake.setFood(food);
        food.setSnake(snake);
        //生成食物数量
        food.init(15);

        user = NettyUtils.getAttr(User.class, "user", channel);
        game = new Game();
        game.setMode(0);
        game.setStart_length(snake.length());
        game.setStart_time(LocalDateTime.now());
        game.setState(0);
        game.setUser_id(user.getId());
        int result = manager.getGameService().insertGame(game);
        if (result < 1) {
            throw new RuntimeException("启动游戏异常");
        }

        setGameRunner();
        setGameFrame();

        //初始化命令队列
        commandQueue = new LinkedList<>();
    }

    public void stopGameFrame() {
        if (gameFrame.isCancellable()) {
            gameFrame.cancel(true);
        }
    }

    public void stopGameLogout() {
        if (gameLogout != null && gameLogout.isCancellable()) {
            gameLogout.cancel(true);
        }
    }

    public void stopGameRunner() {
        if (gameRunner.isCancellable()) {
            gameRunner.cancel(true);
        }
    }

    public void setGameFrame() {
        gameFrame = executor.scheduleAtFixedRate(new GameFrame(), 0, 1000 / snake.getSpeed(), TimeUnit.MILLISECONDS);
    }

    public void setGameRunner() {
        gameRunner = executor.scheduleAtFixedRate(new GameRunner(), 0, 1000 / snake.getSpeed(), TimeUnit.MILLISECONDS);
    }

    public void setGameLogout() {
        gameLogout = executor.schedule(new gameLogout(), 5, TimeUnit.MINUTES);
    }

    public void resetSpeed() {
        if (gameRunner.isCancellable()) {
            gameRunner.cancel(true);
        }

        if (gameFrame.isCancellable()) {
            gameFrame.cancel(true);
        }
        setGameFrame();
        setGameRunner();
    }


    class gameLogout implements Runnable {
        @Override
        public void run() {
            snake.setDeath(true);
        }
    }


    class GameRunner implements Runnable {
        @Override
        public void run() {
            try {
                //执行用户命令
                if (commandQueue.size() != 0) {
                    Integer pop = commandQueue.pop();
                    snake.setDirection(pop);
                    incrCommandTime();
                }

                //贪吃蛇前进
                snake.go();

                //是否死亡
                if (snake.isDeath()) {
                    //结算
                    close(2);
                }
            } catch (Exception e) {
                log.error("gamerunner", e);
            }
        }
    }

    class GameFrame implements Runnable {
        private int count = 0;

        @Override
        public void run() {
            log.debug("gameframe run");
            HashMap<String, String> map = new HashMap<>();
            if (count == 0) {
                //首次加载数据到客户端
                map.put("window", JsonUtils.serialize(window));
                map.put("snake", JsonUtils.serialize(snake));
                map.put("food", JsonUtils.serialize(food));
                channel.writeAndFlush(ProtoUtils.getSingle("game", JsonUtils.serialize(map)));
                food.setChange(false);
            } else {
                if (food.isChange()) {
                    map.put("food", JsonUtils.serialize(food));
                    food.setChange(false);
                }
                map.put("snake", JsonUtils.serialize(snake));
                channel.writeAndFlush(ProtoUtils.getSingle("game", JsonUtils.serialize(map)));
            }
            count++;
        }
    }

    //结算
    public void close(Integer state) {
        //更新游戏表状态
        game.setState(state);
        game.setEnd_length(snake.length());
        game.setEnd_time(LocalDateTime.now());
        game.setGrade(game.generGrade());
        game.setCommand_time(this.commandTime);
        manager.getGameService().updateGame(game);
        //结算分数
        channel.writeAndFlush(ProtoUtils.getSingle("grade", game.getGrade().toString()));
        //结算经验
        int newexp = game.getGrade() * 2;
        user.setExp(user.getExp() + newexp);
        manager.getUserService().updateUser(user);
        channel.writeAndFlush(ProtoUtils.getSingle("exp", ""+newexp));
        //结算时间
        channel.writeAndFlush(ProtoUtils.getSingle("time", game.playTime()));
        //更新用户信息
        channel.writeAndFlush(ProtoUtils.getSingle("user", JsonUtils.serialize(user)));

        //关闭计时器
        if (gameFrame.isCancellable()) {
            gameFrame.cancel(true);
        }
        if (gameLogout != null && gameLogout.isCancellable()) {
            gameLogout.cancel(true);
        }
        if (gameRunner.isCancellable()) {
            gameRunner.cancel(true);
        }
        //清除内存
        manager.remove(user.getId());
    }


    public void incrCommandTime() {
        this.commandTime++;
    }


}

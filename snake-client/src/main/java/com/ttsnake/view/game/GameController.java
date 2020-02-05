package com.ttsnake.view.game;

import com.ttsnake.SnakeClientApplication;
import com.ttsnake.common.SnakeProto;
import com.ttsnake.common.utils.JsonUtils;
import com.ttsnake.common.utils.ProtoUtils;
import com.ttsnake.common.vo.ChannelVO;
import com.ttsnake.netty.Client;
import com.ttsnake.view.user.UserView;
import de.felixroske.jfxsupport.FXMLController;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@FXMLController
@Slf4j
public class GameController extends SimpleChannelInboundHandler<SnakeProto.SnakeMessage> {

    @Autowired
    private Client client;

    @FXML
    private Canvas canvas;
    @FXML
    private VBox scoreboard;
    @FXML
    private Label grade;
    @FXML
    private Label exp;
    @FXML
    private Label time;

    private WindowDTO windowdto;
    private FoodDTO fooddto;
    private SnakeDTO snakedto;

    private boolean keyPre = false;

    @FXML
    private void continueGame(Event event) {
        scoreboard.setVisible(false);
        SnakeClientApplication.showView(UserView.class);
        SnakeClientApplication.getStage().sizeToScene();
    }


    @FXML
    private void keycommandPre(KeyEvent event) {
        if (!keyPre) {
            Channel channel = client.getChannel();
            KeyCode code = event.getCode();
            if ("UP".equals(code.toString())) {
                SnakeProto.SnakeMessage single = ProtoUtils.getSingle("command.direction", "0");
                channel.writeAndFlush(single);
            } else if ("DOWN".equals(code.toString())) {
                SnakeProto.SnakeMessage single = ProtoUtils.getSingle("command.direction", "1");
                channel.writeAndFlush(single);
            } else if ("LEFT".equals(code.toString())) {
                SnakeProto.SnakeMessage single = ProtoUtils.getSingle("command.direction", "2");
                channel.writeAndFlush(single);
            } else if ("RIGHT".equals(code.toString())) {
                SnakeProto.SnakeMessage single = ProtoUtils.getSingle("command.direction", "3");
                channel.writeAndFlush(single);
            }
            keyPre = true;
        }
    }

    @FXML
    private void keycommandRel() {
        keyPre = false;
    }

    private void flash() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                draw(gc);
            }
        });
    }

    private void draw(GraphicsContext gc) {
        Integer side = windowdto.getSide();

        //画背景
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, windowdto.getWidth(), windowdto.getHeight());

        //画食物
        List<Map<String, Double>> foods = fooddto.getFoods();
        foods.forEach(map -> {
            gc.setFill(Color.PINK);
            double x = map.get("x");
            double y = map.get("y");
            gc.fillRect(x, y, side, side);
        });

        //画蛇
        List<Map<String, Double>> body = snakedto.getBody();
        gc.setFill(Color.RED);
        body.forEach(map -> {
            double x = map.get("x");
            double y = map.get("y");
            gc.fillRect(x, y, side, side);
        });
    }

    private Color randomColor() {
        double r = Math.random();
        double g = Math.random();
        double b = Math.random();
        return new Color(r, g, b, 1);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SnakeProto.SnakeMessage msg) throws Exception {
        if (msg.getDataType() == SnakeProto.SnakeMessage.DataType.Single) {
            SnakeProto.Single single = msg.getSingle();
            String type = single.getType();
            if ("game".equals(type)) {
                Map<String, String> map = JsonUtils.parseMap(single.getJson(), String.class, String.class);
                map.forEach((k, v) -> {
                    if ("window".equals(k)) {
                        log.debug("window-----:" + v);
                        windowdto = JsonUtils.parse(v, WindowDTO.class);
                        //初始化宽高
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                canvas.setWidth(windowdto.getWidth());
                                canvas.setHeight(windowdto.getHeight());
                                SnakeClientApplication.getStage().sizeToScene();
                            }
                        });
                    } else if ("snake".equals(k)) {
                        log.debug("snake-----:" + v);
                        snakedto = JsonUtils.parse(v, SnakeDTO.class);
                    } else if ("food".equals(k)) {
                        log.debug("food-----:" + v);
                        fooddto = JsonUtils.parse(v, FoodDTO.class);
                    }
                });

                flash();
            } else if ("grade".equals(type)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        scoreboard.setVisible(true);
                        grade.setText(single.getJson());
                    }
                });
            } else if ("exp".equals(type)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        exp.setText(single.getJson());
                    }
                });
            } else if ("time".equals(type)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        time.setText(single.getJson());
                    }
                });
            }
        }

        ctx.fireChannelRead(msg);
    }
}

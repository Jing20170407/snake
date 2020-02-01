package com.ttsnake.view.user;

import com.ttsnake.SnakeClientApplication;
import com.ttsnake.common.SnakeProto;
import com.ttsnake.common.pojo.User;
import com.ttsnake.common.utils.JsonUtils;
import com.ttsnake.common.utils.NettyUtils;
import com.ttsnake.common.utils.ProtoUtils;
import com.ttsnake.netty.Client;
import com.ttsnake.view.game.GameView;
import de.felixroske.jfxsupport.FXMLController;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

@FXMLController
@Slf4j
public class UserController extends SimpleChannelInboundHandler<SnakeProto.SnakeMessage> {

    @FXML
    private Text name;
    @FXML
    private Label level;
    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label usertip;

    @Autowired
    private Client client;


    @FXML
    private void play(Event event) {
        //请求游戏
        usertip.setText("请稍等...");
        Channel channel = client.getChannel();
        SnakeProto.SnakeMessage request = ProtoUtils.getGameRequest(NettyUtils.getAttr(String.class, "sequence", channel), 0);
        channel.writeAndFlush(request);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("网络连接中断");
                alert.showAndWait();

                System.exit(0);
            }
        });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SnakeProto.SnakeMessage msg) throws Exception {
        if (msg.getDataType() == SnakeProto.SnakeMessage.DataType.Single) {
            SnakeProto.Single single = msg.getSingle();
            String type = single.getType();
            if ("user".equals(type)) {
                User user = JsonUtils.parse(single.getJson(), User.class);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        name.setText(user.getUsername());
                        level.setText("Lv "+user.getLevel().intValue());

                        double pro = user.getLevel_exp() / user.expContain();
                        if (user.expContain() <= 0) {
                            pro = 0;
                        }
                        progressBar.setProgress(pro);
                    }
                });
            }
        } else if (msg.getDataType() == SnakeProto.SnakeMessage.DataType.GameResponse) {
            SnakeProto.GameResponse response = msg.getGameResponse();
            String code = response.getCode();
            if ("200".equals(code)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        SnakeClientApplication.showView(GameView.class);
                    }
                });
            }

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    log.debug(response.getCode()+":"+response.getMsg());
                    usertip.setText(response.getMsg());
                }
            });

            ctx.executor().schedule(new Runnable() {
                @Override
                public void run() {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            usertip.setText("");
                        }
                    });
                }
            }, 5, TimeUnit.SECONDS);
        }

        ctx.fireChannelRead(msg);
    }
}

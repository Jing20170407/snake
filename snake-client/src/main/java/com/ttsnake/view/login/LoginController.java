package com.ttsnake.view.login;

import com.ttsnake.SnakeClientApplication;
import com.ttsnake.common.SnakeProto;
import com.ttsnake.common.utils.CodecUtils;
import com.ttsnake.common.utils.NettyUtils;
import com.ttsnake.common.utils.ProtoUtils;
import com.ttsnake.netty.Client;
import com.ttsnake.view.user.UserView;
import de.felixroske.jfxsupport.FXMLController;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@FXMLController
@Slf4j
public class LoginController extends SimpleChannelInboundHandler<SnakeProto.SnakeMessage> {

    @Autowired
    private Client client;

    @FXML
    private TextField username;

    @FXML
    private Label tip;

    @FXML
    private PasswordField password;


    @FXML
    private void login(Event event) {
        Channel channel = client.getChannel();
        if (channel == null || !channel.isActive()) {
            tip.setText("网络未连接");
        } else {
            String username = this.username.getText();
            String password = CodecUtils.md5Hex(this.password.getText());
            channel.writeAndFlush(ProtoUtils.getLoginRequest(username, password));
            tip.setText("登录中. . . . . .");
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SnakeProto.SnakeMessage msg) throws Exception {
        if (msg.getDataType() == SnakeProto.SnakeMessage.DataType.LoginResponse) {
            SnakeProto.LoginResponse response = msg.getLoginResponse();

            String code = response.getCode();

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    tip.setText(response.getMsg());
                }
            });

            if ("200".equals(code)) {
                NettyUtils.setAttr("sequence", response.getSequence(), ctx.channel());
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        SnakeClientApplication.showView(UserView.class);
                        SnakeClientApplication.getStage().sizeToScene();
                    }
                });
            }
        }


        ctx.fireChannelRead(msg);
    }
}

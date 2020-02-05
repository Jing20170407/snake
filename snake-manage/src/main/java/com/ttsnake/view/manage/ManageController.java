package com.ttsnake.view.manage;

import com.ttsnake.common.SnakeProto;
import com.ttsnake.common.utils.JsonUtils;
import com.ttsnake.common.utils.ProtoUtils;
import com.ttsnake.common.vo.ChannelVO;
import com.ttsnake.netty.Client;
import de.felixroske.jfxsupport.FXMLController;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@FXMLController
public class ManageController extends SimpleChannelInboundHandler<SnakeProto.SnakeMessage> {

    @Autowired
    private Client client;

    @FXML
    private ListView listview;

    @FXML
    private void flush() {
        Channel channel = client.getChannel();
        channel.writeAndFlush(ProtoUtils.getSingle("channelSet", ""));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SnakeProto.SnakeMessage msg) throws Exception {
        if (msg.getDataType() == SnakeProto.SnakeMessage.DataType.Single) {
            SnakeProto.Single single = msg.getSingle();
            String type = single.getType();
            if ("channelSet".equals(type)) {
                String json = single.getJson();
                List<ChannelVO> list = JsonUtils.parseList(json, ChannelVO.class);


                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ObservableList items = listview.getItems();
                        items.remove(0,items.size());
                        items.addAll(list.toArray());
                    }
                });

            }
        }
    }
}

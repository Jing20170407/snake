package com.ttsnake;


import com.ttsnake.view.manage.ManageView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.SplashScreen;
import io.netty.channel.ChannelFuture;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.ttsnake.netty.Client;

@SpringBootApplication
@Slf4j
public class SnakeManageApplication extends AbstractJavaFxApplicationSupport implements CommandLineRunner {

    @Autowired
    private Client client;

    @Value(value = "${netty.server.host}")
    private String host;

    @Value(value = "${netty.server.port}")
    private int port;

    public static void main(String[] args) {
        launch(SnakeManageApplication.class, ManageView.class, new SplashScreen(){
                    @Override
                    public boolean visible() {
                        return false;
                    }
                }, args);
    }


    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }

    @Override
    public void run(String... args) throws Exception {
        ChannelFuture future = client.init(host, port);
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                log.info("stop netty ...");
                client.destroy();
            }
        });
        future.channel().closeFuture().addListener(f -> {
            if (f.isSuccess()) {
                log.info("close connect");
            }
        });
    }

    /*@Override
    public void start(Stage stage) throws Exception {
        stage.sizeToScene();
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("icon.png")));
        super.start(stage);
    }*/
}

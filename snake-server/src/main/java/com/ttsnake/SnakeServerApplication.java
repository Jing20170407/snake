package com.ttsnake;

import com.ttsnake.netty.Server;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.ttsnake.mapper")
@Slf4j
public class SnakeServerApplication implements CommandLineRunner {

    @Value("${netty.port}")
    private int port;

    @Autowired
    private Server server;

    public static void main(String[] args) {
        SpringApplication.run(SnakeServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ChannelFuture future = server.init(port);
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                server.destroy();
            }
        });
        future.channel().closeFuture().addListener(f -> {
            if (f.isSuccess()) {
                log.info("close connect");
            }
        });
    }
}

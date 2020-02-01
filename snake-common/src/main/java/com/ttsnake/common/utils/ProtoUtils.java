package com.ttsnake.common.utils;

import com.ttsnake.common.SnakeProto;

public class ProtoUtils {
    public static SnakeProto.SnakeMessage getLoginResponce(String code, String sequence, String msg) {
        return SnakeProto.SnakeMessage.newBuilder()
                .setDataType(SnakeProto.SnakeMessage.DataType.LoginResponse)
                .setLoginResponse(
                        SnakeProto.LoginResponse.newBuilder()
                                .setCode(code)
                                .setSequence(sequence)
                                .setMsg(msg)
                                .build()
                ).build();
    }

    public static SnakeProto.SnakeMessage getLoginRequest(String username, String password) {
        return SnakeProto.SnakeMessage.newBuilder()
                .setDataType(SnakeProto.SnakeMessage.DataType.LoginRequest)
                .setLoginRequest(
                        SnakeProto.LoginRequest.newBuilder()
                                .setUsername(username)
                                .setPassword(password)
                                .build()
                ).build();
    }

    public static SnakeProto.SnakeMessage getGameResponce(String code,String msg) {
        return SnakeProto.SnakeMessage.newBuilder()
                .setDataType(SnakeProto.SnakeMessage.DataType.GameResponse)
                .setGameResponse(
                        SnakeProto.GameResponse.newBuilder()
                                .setCode(code)
                                .setMsg(msg)
                                .build()
                ).build();
    }

    public static SnakeProto.SnakeMessage getGameRequest(String sequence, Integer mode) {
        return SnakeProto.SnakeMessage.newBuilder()
                .setDataType(SnakeProto.SnakeMessage.DataType.GameRequest)
                .setGameRequest(
                        SnakeProto.GameRequest.newBuilder()
                                .setSequence(sequence)
                                .setMode(mode)
                                .build()
                ).build();
    }

    public static SnakeProto.SnakeMessage getSingle(String type, String json) {
        return SnakeProto.SnakeMessage.newBuilder()
                .setDataType(SnakeProto.SnakeMessage.DataType.Single)
                .setSingle(
                        SnakeProto.Single.newBuilder()
                                .setType(type)
                                .setJson(json)
                                .build()
                ).build();
    }
}

package com.ttsnake.common.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelVO {
    private boolean isActive;//活动状态(active)且已连接(connect)
    private boolean isInputShutdown;
    private boolean isOutputShutdown;
    private boolean isShutdown;//确定此通道的输入和输出是否都已关闭。
    private boolean isOpen;//处于打开状态(open)，并且稍后可能处于活动状态(active)
    private boolean isRegistered;//Channel已向EventLoop注册
    private boolean isWritable;//立即可写入

    @Override
    public String toString() {
        return "ChannelVO{" +
                "isActive=" + isActive +
                ", isInputShutdown=" + isInputShutdown +
                ", isOutputShutdown=" + isOutputShutdown +
                ", isShutdown=" + isShutdown +
                ", isOpen=" + isOpen +
                ", isRegistered=" + isRegistered +
                ", isWritable=" + isWritable +
                '}';
    }
}

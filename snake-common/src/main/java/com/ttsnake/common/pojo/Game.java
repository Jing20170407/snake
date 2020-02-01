package com.ttsnake.common.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Duration;
import java.time.LocalDateTime;

@Table
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long user_id;
    private Integer mode;//当前游戏的模式：0、为经典模式，1、为多人模式，2、为道具模式
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private Integer start_length;
    private Integer end_length;
    private Integer command_time;//用户命令次数
    private Integer grade;//游戏结束机算的分数（计算公式：end_length-start_length+（end_length-start_length/end_time-start_time））
    private Integer state;//游戏状态：0、为游戏进行中，1、用户放弃或中断游戏，2、用户完成游戏


    public void incr_command() {
        if (command_time == null) {
            command_time = 1;
        } else {
            command_time++;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public LocalDateTime getStart_time() {
        return start_time;
    }

    public void setStart_time(LocalDateTime start_time) {
        this.start_time = start_time;
    }

    public LocalDateTime getEnd_time() {
        return end_time;
    }

    public void setEnd_time(LocalDateTime end_time) {
        this.end_time = end_time;
    }

    public Integer getStart_length() {
        return start_length;
    }

    public void setStart_length(Integer start_length) {
        this.start_length = start_length;
    }

    public Integer getEnd_length() {
        return end_length;
    }

    public void setEnd_length(Integer end_length) {
        this.end_length = end_length;
    }

    public Integer getCommand_time() {
        return command_time;
    }

    public void setCommand_time(Integer command_time) {
        this.command_time = command_time;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer generGrade() {
        int length = end_length-start_length;
        Duration between = Duration.between(start_time, end_time);
        int time = (int)between.getSeconds();
        return length + (length*1000 / time);
    }

    public String playTime() {
        Duration between = Duration.between(start_time, end_time);
        long seconds = between.getSeconds();

        String hour = ""+(seconds/3600);
        String minute = ""+(seconds%3600/60);
        String second = ""+(seconds%3600%60);

        hour = hour.length() < 2 ? "0" + hour : hour;
        minute = minute.length() < 2 ? "0" + minute : minute;
        second = second.length() < 2 ? "0" + second : second;

        String result = "";
        if (!"00".equals(hour)) {
            result += hour+"时";
        }
        if (!"00".equals(minute)) {
            result += minute+"分";
        }
        result += second + "秒";

        return result;
    }
}

package com.ttsnake.view.game;

import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class SnakeDTO {
    private Integer speed = 1;//格每秒
    private Integer direction;//贪吃蛇前进方向：0、上，1、下，2、左，3、右。
    private List<Map<String,Double>> body;
    private Integer side;//贪吃蛇单个方块的边长，单位像素
}

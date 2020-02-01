package com.ttsnake.common.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.paint.Color;

public class Window {

    private Integer width;//像素宽
    private Integer height;//像素高

    private Integer widthSquare;//宽的方格个数
    private Integer heightSquare;//高的方格个数

    private Integer side;//方格边长

    @JsonIgnore
    private Color color;

    public Window() {
    }

    public Window(Integer widthSquare, Integer heightSquare, Integer side, Color color) {
        this.color = color;
        this.widthSquare = widthSquare;
        this.heightSquare = heightSquare;
        this.side = side;
        this.width = widthSquare * side;
        this.height = heightSquare * side;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWidthSquare() {
        return widthSquare;
    }

    public Integer getHeightSquare() {
        return heightSquare;
    }

    public Integer getSide() {
        return side;
    }

    public Color getColor() {
        return color;
    }
}

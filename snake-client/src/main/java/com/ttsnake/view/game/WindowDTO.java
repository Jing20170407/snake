package com.ttsnake.view.game;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WindowDTO{

    private Integer width;//像素宽
    private Integer height;//像素高

    private Integer widthSquare;//宽的方格个数
    private Integer heightSquare;//高的方格个数

    private Integer side;//方格边长

}

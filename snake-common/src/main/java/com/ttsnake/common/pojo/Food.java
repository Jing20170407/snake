package com.ttsnake.common.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.geometry.Point2D;

import java.util.HashSet;
import java.util.LinkedList;

public class Food {
    private HashSet<Point2D> foods;

    @JsonIgnore
    private Window window;

    @JsonIgnore
    private Snake snake;

    @JsonIgnore
    private boolean change = true;

    public Food(Window window) {
        this.window = window;
        foods = new HashSet<>();
    }

    public Food() {
    }

    public void init(int num) {
        for (int i = 0; i < num; i++) {
            generateFood();
        }
    }

    public HashSet<Point2D> getFoods() {
        return foods;
    }

    public void generateFood() {
        Point2D p;
        Snake.SnakeLinkedListMap body = (Snake.SnakeLinkedListMap)snake.getBody();
        do {
            double x = Math.floor(Math.random() * window.getWidthSquare())*window.getSide();
            double y = Math.floor(Math.random() * window.getHeightSquare())*window.getSide();
            p = new Point2D(x, y);
        } while (body.contain(p) || foods.contains(p));
        foods.add(p);
        change = true;
    }

    public void setSnake(Snake snake) {
        this.snake = snake;
    }

    public boolean isChange() {
        return change;
    }

    public void setChange(boolean change) {
        this.change = change;
    }


}

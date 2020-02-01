package com.ttsnake.common.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class Snake {
    @JsonIgnore
    private Color color;
    private Integer speed = 4;//格每秒
    private Integer direction;//贪吃蛇前进方向：0、上，1、下，2、左，3、右。
    private LinkedList<Point2D> body;
    private Integer side;//贪吃蛇单个方块的边长，单位像素

    @JsonIgnore
    private Window window;
    @JsonIgnore
    private Food food;

    @JsonIgnore
    private boolean death = false;



    public Snake(Window window) {
        this.window = window;
        color=Color.RED;
        direction = 3;
        body = new SnakeLinkedListMap();
        this.side = window.getSide();
        Random random = new Random();
        initBody(random.nextInt(window.getWidthSquare())*side, random.nextInt(window.getHeightSquare())*side, 5);
    }

    public Snake() {

    }

    public void initBody(int x, int y, int len) {
        SnakeLinkedListMap body = (SnakeLinkedListMap) this.body;
        for (int i = 0; i < len; i++) {
            int x2 = x + (i * side);
            body.addFirst(new Point2D(x2, y));
        }
    }

    public int length() {
        return body.size();
    }


    public void go() {
        log.debug("snake go");
        //前进一步
        Point2D first = body.getFirst();
        Point2D last = null;
        if (direction == 0) {
            double y = first.getY() - side;
            if (y < 0) {
                y = (window.getHeightSquare() - 1)*side;
            }
            last = new Point2D(first.getX(), y);
        } else if (direction == 1) {
            double y = first.getY() + side;
            if (y >= window.getHeight()) {
                y = 0;
            }
            last = new Point2D(first.getX(), y);
        } else if (direction == 2) {
            double x = first.getX() - side;
            if (x < 0) {
                x = (window.getWidthSquare()-1) * side;
            }
            last=new Point2D(x, first.getY());
        } else if (direction == 3) {
            double x = first.getX() + side;
            if (x >= window.getWidth()) {
                x = 0;
            }
            last = new Point2D(x, first.getY());
        }

        //是否吃到食物
        SnakeLinkedListMap body = (SnakeLinkedListMap) this.body;
        if (food.getFoods().contains(last)) {
            food.getFoods().remove(last);
            food.generateFood();
        }else {
            body.removeLast();
        }

        //是否死亡
        if (body.contain(last)) {
            death = true;
            return;
        }

        //添加到body
        body.addFirst(last);

    }

    public boolean isDeath() {
        return death;
    }

    public void setDeath(boolean death) {
        this.death = death;
    }

    public LinkedList<Point2D> getBody() {
        return body;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public class SnakeLinkedListMap extends LinkedList<Point2D> {
        private HashSet<Point2D> set;


        public SnakeLinkedListMap() {
            set = new HashSet<>();
        }

        public void addFirst(Point2D point) {
            super.addFirst(point);
            set.add(point);
        }

        public Point2D removeLast() {
            Point2D p = super.removeLast();
            set.remove(p);
            return p;
        }


        public Point2D getFirst() {
            return super.getFirst();
        }


        public boolean contain(Point2D point) {
            return set.contains(point);
        }

        public int size() {
            return set.size();
        }

        public HashSet<Point2D> getSet() {
            return set;
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public void setBody(LinkedList<Point2D> body) {
        this.body = body;
    }

    public Integer getSide() {
        return side;
    }

    public void setSide(Integer side) {
        this.side = side;
    }

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public Food getFood() {
        return food;
    }
}

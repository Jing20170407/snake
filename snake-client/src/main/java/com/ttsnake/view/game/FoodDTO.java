package com.ttsnake.view.game;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class FoodDTO {
    private List<Map<String,Double>> foods;
}

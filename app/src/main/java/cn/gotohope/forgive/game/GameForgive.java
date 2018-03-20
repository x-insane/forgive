package cn.gotohope.forgive.game;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum GameForgive {
    NOTHING(5, ""),
    SPEED_DOWN(15, "speed down"),
    SCORE_ADD_5(50, "+5"),
    SCORE_ADD_10(20, "+10"),
    SCORE_ADD_20(10, "+20"),
    SCORE_ADD_100(2, "+100"),
    SPEED_UP(5, "speed up"),
    GAME_OVER(1, "炸弹！");

    private int weight;
    private String tip;
    private int bound;
    GameForgive(int weight, String tip) {
        this.weight = weight;
        this.tip = tip;
    }

    @Override
    public String toString() {
        return tip;
    }

    private static int size = 0;
    private static List<GameForgive> data = Collections.unmodifiableList(Arrays.asList(values()));
    private static Random rand = new Random(System.currentTimeMillis());
    static {
        for (GameForgive item : data) {
            size += item.weight;
            item.bound = size;
        }
    }
    public static GameForgive choose() {
        int chooser = rand.nextInt(size);
        for (GameForgive item : data) {
            if (item.bound > chooser)
                return item;
        }
        return NOTHING;
    }

}

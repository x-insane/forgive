package cn.gotohope.forgive.game;

import java.util.Random;

import cn.gotohope.forgive.data.Game;

/**
 * Created by xinsane on 2018/2/28.
 */

public class GameViewData {

    private Random rand = new Random(System.currentTimeMillis());

    private Game game;

    private final int loop;
    private int step = -1;
    private final int[] data;

    private int score = 0;

    private int wrong_x = 0, wrong_y = 0;
    private int wrong_step = 0;

    public GameViewData(Game game) {
        this.game = game;
        loop = game.row_number() * 2 + 1;
        data = new int[loop];
    }

    public int get(int i) {
        i %= loop;
        if (i == (step + 1) % loop) {
            data[i] = rand.nextInt(game.column_number()) + 1;
            int last = i > 0 ? i - 1 : loop - 1;
            while (data[i] == data[last])
                data[i] = rand.nextInt(game.column_number()) + 1;
            step ++;
        }
        return data[i];
    }

    public boolean set(int i) {
        i %= loop;
        int last = i > 0 ? i - 1 : loop - 1;
        if (data[last] == 0) {
            data[i] = 0;
            score ++;
            return true;
        }
        return false;
    }

    public void wrong(int step) {
        wrong_step = step;
    }

    public void wrong(int x, int y) {
        wrong_x = x;
        wrong_y = y;
    }

    public int score() {
        return score;
    }

    public int get_wrong_x() {
        return wrong_x;
    }

    public int get_wrong_y() {
        return wrong_y;
    }

    public int get_wrong_step() {
        return wrong_step;
    }

}

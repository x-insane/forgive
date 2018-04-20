package cn.gotohope.forgive.game;

import java.util.Random;

import cn.gotohope.forgive.data.Game;

class GameViewData {

    private GameListener listener;
    public interface GameListener {
        void onScore();
        void onForgive();
        void onStep(int x, int y);
    }

    private Random rand = new Random(System.currentTimeMillis());
    private Game game;

    private final int loop;
    private int step = -1;
    private final int[] data;

    private int wrong_x = 0, wrong_y = 0;
    private int missing_till = 0;

    GameViewData(Game game, GameListener listener) {
        this.game = game;
        loop = game.row_number * 2 + 1;
        data = new int[loop];
        this.listener = listener;
    }

    int get(int i) {
        i %= loop;
        if (i == (step + 1) % loop) {
            data[i] = rand.nextInt(game.column_number) + 1;
            int last = i > 0 ? i - 1 : loop - 1;
            while (data[i] == Math.abs(data[last]))
                data[i] = rand.nextInt(game.column_number) + 1;
            if (game.has_forgive_till && data[last] > 0 && rand.nextInt(game.forgive_bound) == 0)
                data[i] = -data[i];
            step ++;
        }
        return data[i];
    }

    boolean set(int y) {
        int i = y % loop;
        int last = i > 0 ? i - 1 : loop - 1;
        int last_last = last > 0 ? last -1 : loop - 1;
        if (data[last] == 0 || (data[last] < 0 && data[last_last] == 0)) {
            if (data[i] < 0)
                listener.onForgive();
            listener.onScore();
            listener.onStep(Math.abs(data[i]), y);
            data[i] = 0;
            return true;
        }
        return false;
    }

    void wrong(int missing_till) {
        this.missing_till = missing_till;
    }

    void wrong(int x, int y) {
        wrong_x = x;
        wrong_y = y;
    }

    int get_wrong_x() {
        return wrong_x;
    }

    int get_wrong_y() {
        return wrong_y;
    }

    int get_missing_till() {
        return missing_till;
    }

}

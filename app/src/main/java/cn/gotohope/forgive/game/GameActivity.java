package cn.gotohope.forgive.game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.gotohope.forgive.R;
import cn.gotohope.forgive.data.Game;

public class GameActivity extends AppCompatActivity {

    public Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        game = (Game) getIntent().getSerializableExtra("game");
        setContentView(R.layout.activity_game);
    }

}

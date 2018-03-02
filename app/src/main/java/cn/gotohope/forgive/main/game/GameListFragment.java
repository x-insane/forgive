package cn.gotohope.forgive.main.game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import cn.gotohope.forgive.R;
import cn.gotohope.forgive.data.Game;
import cn.gotohope.forgive.util.FileManager;

public class GameListFragment extends Fragment {

    private List<Game> list;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (list == null)
            loadGames();
        recyclerView = getView().findViewById(R.id.game_list_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getView().getContext());
        recyclerView.setLayoutManager(layoutManager);
        GameListAdapter adapter = new GameListAdapter(list, this);
        recyclerView.setAdapter(adapter);

//        Toolbar toolbar = getView().findViewById(R.id.game_list_toolbar);
//        toolbar.setTitle("基本游戏关卡");
//        toolbar.setTitleTextColor(0xffffffff);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        GameListAdapter.ViewHolder holder = (GameListAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(requestCode);
        holder.gameBest.setText("Best: " + data.getIntExtra("best", 0));
    }

    private void loadGames() {
        list = new ArrayList<>();
        String json = FileManager.readAsset(getContext(), "game_list.json");
        if (json == null)
            return;
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray data = parser.parse(json).getAsJsonArray();
        for (JsonElement e : data) {
            Game item = gson.fromJson(e, Game.class);
            item.best = getContext().getSharedPreferences("max_score", Context.MODE_PRIVATE).getInt(item.getId(), 0);
            list.add(item);
        }
    }

}

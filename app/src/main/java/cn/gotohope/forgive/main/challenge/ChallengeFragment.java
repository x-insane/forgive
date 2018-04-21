package cn.gotohope.forgive.main.challenge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.xinsane.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.gotohope.forgive.App;
import cn.gotohope.forgive.R;
import cn.gotohope.forgive.data.Game;
import cn.gotohope.forgive.util.FileManager;

public class ChallengeFragment extends Fragment {

    private static List<Game> list = new ArrayList<>();
    private static ChallengeAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_challenge, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadChallenge();
        RecyclerView recyclerView = view.findViewById(R.id.challenge_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChallengeAdapter(list, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        Game game = list.get(requestCode);
        game.best_v = data.getFloatExtra("best", 0);
        adapter.notifyItemChanged(requestCode);
    }

    private static void loadChallenge() {
        list = new ArrayList<>();
        String json = FileManager.readAsset(App.getContext(), "challenge.json");
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray data = parser.parse(json).getAsJsonArray();
        SharedPreferences preference = App.getContext().getSharedPreferences("max_velocity", Context.MODE_PRIVATE);
        for (JsonElement e : data) {
            Game item = gson.fromJson(e, Game.class);
            item.best_v = preference.getFloat(item.id, 0);
            list.add(item);
        }
    }

    public static void freshList() {
        loadChallenge();
        if (adapter != null)
            adapter.notifyItemRangeChanged(0, adapter.getItemCount());
    }

}

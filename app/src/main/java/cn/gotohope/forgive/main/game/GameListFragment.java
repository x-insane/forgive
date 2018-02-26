package cn.gotohope.forgive.main.game;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.gotohope.forgive.R;
import cn.gotohope.forgive.data.Game;

public class GameListFragment extends Fragment {

    private List<Game> list;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (list == null)
            loadGames();
        RecyclerView recyclerView = getView().findViewById(R.id.game_list_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getView().getContext());
        recyclerView.setLayoutManager(layoutManager);
        GameListAdapter adapter = new GameListAdapter(list, getContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_list, container, false);
    }

    private void loadGames() {
        list = new ArrayList<>();
        for (int i=0;i<100;++i) {
            Game item = new Game();
            list.add(item);
        }
    }

}

package cn.gotohope.forgive.main.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.gotohope.forgive.R;
import cn.gotohope.forgive.data.Game;
import cn.gotohope.forgive.game.GameActivity;

@SuppressLint("SetTextI18n")
public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder> {

    private List<Game> list;
    private Fragment fragment; // context of GameListFragment

    GameListAdapter(List<Game> list, Fragment context) {
        this.list = list;
        fragment = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(fragment.getContext(), GameActivity.class);
                intent.putExtra("game", list.get(position));
                fragment.startActivityForResult(intent, position);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Game item = list.get(position);
        holder.gameId.setText(String.valueOf(position + 1));
        holder.gameName.setText(item.name);
        if (list.get(position).best > 0)
            holder.gameBest.setText("Best: " + list.get(position).best);
        else
            holder.gameBest.setText("");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView gameName;
        TextView gameId;
        TextView gameBest;

        ViewHolder(View view) {
            super(view);
            gameName = view.findViewById(R.id.game_list_item_name);
            gameId = view.findViewById(R.id.game_list_item_id);
            gameBest = view.findViewById(R.id.game_list_item_best);
        }

    }

}

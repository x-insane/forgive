package cn.gotohope.forgive.main.game;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xinsane.util.LogUtil;

import java.util.List;

import cn.gotohope.forgive.R;
import cn.gotohope.forgive.data.Game;
import cn.gotohope.forgive.game.GameActivity;

/**
 * Created by xinsane on 2018/2/18.
 */

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
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Game item = list.get(position);
        holder.gameId.setText(String.valueOf(position + 1));
        holder.gameName.setText(item.getName()+" <"+item.getId()+">");
        if (list.get(position).best > 0)
            holder.gameBest.setText("Best: " + list.get(position).best);
        else
            holder.gameBest.setText("");
        holder.gameStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(fragment.getContext(), GameActivity.class);
                intent.putExtra("game", list.get(position));
                fragment.startActivityForResult(intent, position);
//                LogUtil.d("position: " + position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView gameName;
        TextView gameId;
        TextView gameBest;
        LinearLayout gameStart;

        ViewHolder(View view) {
            super(view);
            gameName = view.findViewById(R.id.game_list_item_name);
            gameId = view.findViewById(R.id.game_list_item_id);
            gameStart = view.findViewById(R.id.game_list_item_start);
            gameBest = view.findViewById(R.id.game_list_item_best);
        }

    }

}

package cn.gotohope.forgive;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

/**
 * Created by xinsane on 2018/2/18.
 */

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder> {

    private List<GameItem> list;
    private Random rand = new Random(System.currentTimeMillis());
    private Context context; // context of GameListFragment

    GameListAdapter(List<GameItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        GameItem item = list.get(position);
        holder.gameId.setText(String.valueOf(position + 1));
        holder.gameStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "开始第 " + (position+1) + " 关游戏", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, GameActivity.class);
                context.startActivity(intent);
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
        TextView gameStart;

        ViewHolder(View view) {
            super(view);
            gameName = (TextView) view.findViewById(R.id.game_list_item_name);
            gameId = (TextView) view.findViewById(R.id.game_list_item_id);
            gameStart = (TextView) view.findViewById(R.id.game_list_item_start);
        }

    }

}

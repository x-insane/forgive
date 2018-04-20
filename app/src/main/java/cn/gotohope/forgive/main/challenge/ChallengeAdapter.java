package cn.gotohope.forgive.main.challenge;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.gotohope.forgive.R;
import cn.gotohope.forgive.data.Game;
import cn.gotohope.forgive.game.GameActivity;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ViewHolder> {
    
    private List<Game> list;
    private Fragment fragment; // context of ChallengeFragment

    ChallengeAdapter(List<Game> list, Fragment context) {
        this.list = list;
        fragment = context;
    }

    @Override
    public ChallengeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.challenge_item, parent, false);
        final ChallengeAdapter.ViewHolder holder = new ChallengeAdapter.ViewHolder(view);
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
    public void onBindViewHolder(final ChallengeAdapter.ViewHolder holder, final int position) {
        Game item = list.get(position);
        holder.gameId.setText(String.valueOf(position + 1));
        holder.gameName.setText(item.name);
        if (list.get(position).best_v > 0)
            holder.gameBest.setText(String.format("Best: %.3f tills/s", list.get(position).best_v));
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
            gameName = view.findViewById(R.id.challenge_item_name);
            gameId = view.findViewById(R.id.challenge_item_id);
            gameBest = view.findViewById(R.id.challenge_item_best);
        }

    }

}

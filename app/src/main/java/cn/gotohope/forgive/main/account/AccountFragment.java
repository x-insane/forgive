package cn.gotohope.forgive.main.account;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.gotohope.forgive.R;
import cn.gotohope.forgive.main.MainActivity;
import cn.gotohope.forgive.user.UserManager;

public class AccountFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView text_nickname = getView().findViewById(R.id.text_nickname);
        text_nickname.setText(UserManager.getUser().getNickname());
        LinearLayout btn_edit_info = getView().findViewById(R.id.btn_edit_info);
        LinearLayout btn_logout = getView().findViewById(R.id.btn_logout);
        btn_edit_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                    .setTitle("确认退出登录")
                    .setCancelable(true)
                    .setMessage("退出后将会清除游戏数据，确认退出登录吗？")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .setPositiveButton("退出登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            UserManager.logout();
                            ((MainActivity) getActivity()).navigation.setSelectedItemId(R.id.navigation_game_list);
                            dialogInterface.dismiss();
                        }
                    }).show();
            }
        });
    }
}

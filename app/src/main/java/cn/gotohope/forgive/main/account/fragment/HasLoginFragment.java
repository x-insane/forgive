package cn.gotohope.forgive.main.account.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.gotohope.forgive.Config;
import cn.gotohope.forgive.R;
import cn.gotohope.forgive.main.account.AccountFragment;
import cn.gotohope.forgive.user.ModifyUserActivity;
import cn.gotohope.forgive.user.UserManager;

public class HasLoginFragment extends Fragment {

    private View mView = null;
    private TextView textView_nickname, textView_description;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView != null)
            return mView;
        mView = inflater.inflate(R.layout.fragment_has_login, container, false);
        textView_nickname = mView.findViewById(R.id.text_nickname);
        textView_description = mView.findViewById(R.id.text_description);
        LinearLayout btn_edit_info = mView.findViewById(R.id.btn_edit_info);
        btn_edit_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), ModifyUserActivity.class), 1);
            }
        });
        LinearLayout btn_logout = mView.findViewById(R.id.btn_logout);
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
                                dialogInterface.dismiss();
                                ((AccountFragment) getParentFragment()).replaceFragment(false);
                            }
                        }).show();
            }
        });
        LinearLayout btn_browser_rank = mView.findViewById(R.id.btn_browser_rank);
        btn_browser_rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Config.rank_address));
                startActivity(intent);
            }
        });
        refreshData();
        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK)
            refreshData();
    }

    public void refreshData() {
        UserManager.User user = UserManager.getUser();
        if (user != null && mView != null) {
            textView_nickname.setText(user.nickname);
            textView_description.setText(user.description);
        }
    }

}

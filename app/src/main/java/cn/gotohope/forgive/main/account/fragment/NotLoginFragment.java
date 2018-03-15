package cn.gotohope.forgive.main.account.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cn.gotohope.forgive.Config;
import cn.gotohope.forgive.R;
import cn.gotohope.forgive.main.account.AccountFragment;
import cn.gotohope.forgive.user.LoginActivity;
import cn.gotohope.forgive.user.UserManager;

public class NotLoginFragment extends Fragment {

    private View mView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView != null)
            return mView;
        mView = inflater.inflate(R.layout.fragment_not_login, container, false);
        LinearLayout btn_login = mView.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), LoginActivity.class), 1);
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
        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && UserManager.isLogin())
            ((AccountFragment) getParentFragment()).replaceFragment(true);
    }

}

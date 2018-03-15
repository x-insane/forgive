package cn.gotohope.forgive.main.account;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xinsane.util.LogUtil;

import cn.gotohope.forgive.R;
import cn.gotohope.forgive.main.account.fragment.HasLoginFragment;
import cn.gotohope.forgive.main.account.fragment.NotLoginFragment;
import cn.gotohope.forgive.user.UserManager;

public class AccountFragment extends Fragment {

    private View mView = null;
    private HasLoginFragment hasLoginFragment = new HasLoginFragment();
    private NotLoginFragment notLoginFragment = new NotLoginFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.d(UserManager.isLogin() ? "has login" : "not login", "account/onCreateView");
        if (mView != null)
            return mView;
        return mView = inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        replaceFragment(UserManager.isLogin());
    }

    public void replaceFragment(boolean isLogin) {
        if (isLogin)
            hasLoginFragment.refreshData();
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.account_main_content, isLogin ? hasLoginFragment : notLoginFragment);
        transaction.commit();
    }
}

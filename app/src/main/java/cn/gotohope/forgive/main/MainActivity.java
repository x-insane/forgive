package cn.gotohope.forgive.main;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import cn.gotohope.forgive.R;
import cn.gotohope.forgive.main.account.AccountFragment;
import cn.gotohope.forgive.main.challenge.ChallengeFragment;
import cn.gotohope.forgive.main.game.GameListFragment;
import cn.gotohope.forgive.user.LoginActivity;
import cn.gotohope.forgive.user.UserManager;

public class MainActivity extends AppCompatActivity {

    private AccountFragment accountFragment = new AccountFragment();
    private GameListFragment gameListFragment = new GameListFragment();
    private ChallengeFragment challengeFragment = new ChallengeFragment();

    public BottomNavigationView navigation;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK)
            navigation.setSelectedItemId(R.id.navigation_account);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation = findViewById(R.id.main_navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_account: {
                        if (!UserManager.isLogin()) {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivityForResult(intent, 1);
                            return false;
                        }
                        replaceContent(accountFragment);
                        break;
                    }
                    case R.id.navigation_challenge:
                        replaceContent(challengeFragment);
                        break;
                    case R.id.navigation_game_list:
                        replaceContent(gameListFragment);
                        break;
                }
                return true;
            }
        });
        replaceContent(gameListFragment);
    }

    private void replaceContent(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_content, fragment);
        transaction.commit();
    }

}

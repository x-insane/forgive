package cn.gotohope.forgive;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private AccountFragment accountFragment = new AccountFragment();
    private GameListFragment gameListFragment = new GameListFragment();
    private ChallengeFragment challengeFragment = new ChallengeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.main_navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_account:
                        replaceContent(accountFragment);
                        break;
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

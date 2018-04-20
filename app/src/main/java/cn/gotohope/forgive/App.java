package cn.gotohope.forgive;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.xinsane.util.HttpApi;

import cn.gotohope.forgive.user.UserManager;

public class App extends Application {

    private static App app;
    private static Context context;

    public static App getApp() {
        if (app == null) {
            synchronized (App.class) {
                if (app == null)
                    app = new App();
            }
        }
        return app;
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        attemptLogin(null);
    }

    public static void attemptLogin(HttpApi.Listener listener) {
        SharedPreferences preferences = App.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String phone = preferences.getString("phone", "");
        String password = preferences.getString("password", "");
        if (phone.isEmpty() || password.isEmpty())
            return;
        UserManager.login(phone, password, listener);
    }

}

package cn.gotohope.forgive.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xinsane.util.HttpApi;
import com.xinsane.util.LogUtil;

import cn.gotohope.forgive.App;
import cn.gotohope.forgive.Config;
import cn.gotohope.forgive.data.Game;
import cn.gotohope.forgive.main.challenge.ChallengeFragment;
import cn.gotohope.forgive.main.game.GameListFragment;


public class UserManager {

    public static String TYPE_REGISTER = "register";
    public static String TYPE_RESET_PASSWORD = "reset_passwd";

    private static User user = null;

    public static final class User {
        public String phone = "";
        public String nickname = "";
        public String description = "";
    }

    public static boolean isLogin() {
        return user != null;
    }

    public static User getUser() {
        return user;
    }

    private static void syncScore(JsonArray data, boolean challenge) {
        SharedPreferences.Editor editor;
        if (challenge) {
            editor = App.getContext()
                    .getSharedPreferences("max_velocity", Context.MODE_PRIVATE).edit().clear();
        } else {
            editor = App.getContext()
                    .getSharedPreferences("max_score", Context.MODE_PRIVATE).edit().clear();
        }
        for (JsonElement item : data) {
            JsonObject obj = item.getAsJsonObject();
            String game_id = obj.get("game_id").getAsString();
            if (obj.has("score") && !challenge)
                editor.putInt(game_id, obj.get("score").getAsInt());
            if (obj.has("velocity") && challenge)
                editor.putFloat(game_id, obj.get("velocity").getAsFloat());
        }
        editor.apply();
        if (challenge)
            ChallengeFragment.freshList();
        else
            GameListFragment.freshList();
    }

    public static void login(final String phone, final String password, HttpApi.Listener listener) {
        LogUtil.d("phone = " + phone + ", password = " + password, "UserManager/login");
        new HttpApi(Config.api_address + "/android/login")
            .add("phone", phone)
            .add("passwd", password)
            .addListener(listener)
            .addListener(new HttpApi.Listener() {
                @Override
                public void onResult(HttpApi.Result result) {
                    if (result.isSuccess()) {
                        SharedPreferences.Editor editor = App.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit();
                        editor.putString("phone", phone);
                        editor.putString("password", password);
                        editor.apply();
                        Gson gson = new Gson();
                        user = gson.fromJson(result.getData().get("user").getAsJsonObject(), User.class);
                        JsonArray array = result.getData().get("data").getAsJsonArray();
                        syncScore(array, false);
                        syncScore(array, true);
                    }
                }
            })
            .post();
    }
    public static void logout() {
        App.getContext().getSharedPreferences("max_score", Context.MODE_PRIVATE).edit().clear().apply();
        App.getContext().getSharedPreferences("max_velocity", Context.MODE_PRIVATE).edit().clear().apply();
        App.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit().remove("password").apply();
        user = null;
        GameListFragment.freshList();
        ChallengeFragment.freshList();
    }

    public static void register(String phone, String password, String code, HttpApi.Listener listener) {
        LogUtil.d("phone = " + phone + ", password = " + password + ", code = " + code, "UserManager/register");
        new HttpApi(Config.api_address + "/android/register")
            .add("phone", phone)
            .add("passwd", password)
            .add("code", code)
            .addListener(listener)
            .post();
    }

    public static void resetPassword(String phone, String password, String code, HttpApi.Listener listener) {
        LogUtil.d("phone = " + phone + ", password = " + password + ", code = " + code, "UserManager/reset_password");
        new HttpApi(Config.api_address + "/android/reset_passwd")
            .add("phone", phone)
            .add("passwd", password)
            .add("code", code)
            .addListener(listener)
            .post();
    }

    public static void requestMessage(String phone, String target, HttpApi.Listener listener) {
        new HttpApi(Config.api_address + "/android/" + target + "_request_message")
            .add("token", Config.request_message_token)
            .add("phone", phone)
            .addListener(listener)
            .post();
    }

    public static void uploadScore(Game game, int score, HttpApi.Listener listener) {
        new HttpApi(Config.api_address + "/android/upload_score")
            .add("token", Config.upload_score_token)
            .add("game", game.id)
            .add("name", game.name)
            .add("score", String.valueOf(score))
            .addListener(listener)
            .post();
    }


    @SuppressLint("DefaultLocale")
    public static void uploadChallengeScore(Game game, float max_velocity, HttpApi.Listener listener) {
        new HttpApi(Config.api_address + "/android/upload_score")
            .add("type", "challenge")
            .add("token", Config.upload_score_token)
            .add("game", game.id)
            .add("name", game.name)
            .add("velocity", String.format("%.3f", max_velocity))
            .addListener(listener)
            .post();
    }

    public static void modifyUser(String nickname, String description, HttpApi.Listener listener) {
        new HttpApi(Config.api_address + "/android/modify_user")
            .add("nickname", nickname)
            .add("description", description)
            .addListener(listener)
            .addListener(new HttpApi.Listener() {
                @Override
                public void onResult(HttpApi.Result result) {
                    if (result.isSuccess()) {
                        Gson gson = new Gson();
                        user = gson.fromJson(result.getData().get("user").getAsJsonObject(), User.class);
                    }
                }
            })
            .post();
    }

}

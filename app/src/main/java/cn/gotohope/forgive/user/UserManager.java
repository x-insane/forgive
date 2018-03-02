package cn.gotohope.forgive.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xinsane.util.HttpApi;

import cn.gotohope.forgive.util.Config;

/**
 * Created by xinsane on 2018/3/2.
 */

public class UserManager {

    private static User user = null;

    static final class User {

    }

    public static boolean isLogin() {
        return user != null;
    }

    public static User getUser() {
        return user;
    }

    public static String login(String phone, String password) {
        String json = new HttpApi(Config.api_address + "/android/login")
                .add("phone", phone)
                .add("passwd", password)
                .postForString();
        try {
            JsonObject res = (JsonObject) new JsonParser().parse(json);
            if (res.get("error").getAsString().equals("0")) {
                user = new User();
                return null;
            }
            return res.get("msg").getAsString();
        } catch (Exception e) { }
        return "网络异常，请稍后再试";
    }

    public static String register(String phone, String password, String code) {
        String json = new HttpApi(Config.api_address + "/android/register")
                .add("phone", phone)
                .add("passwd", password)
                .add("code", code)
                .postForString();
        try {
            JsonObject res = (JsonObject) new JsonParser().parse(json);
            if (res.get("error").getAsString().equals("0"))
                return null;
            return res.get("msg").getAsString();
        } catch (Exception e) { }
        return "网络异常，请稍后再试";
    }

    public static boolean requestMessage(String phone) {
        String json = new HttpApi(Config.api_address + "/android/request_message")
                .add("token", Config.request_message_token)
                .add("phone", phone)
                .postForString();
        try {
            JsonObject res = (JsonObject) new JsonParser().parse(json);
            if (res.get("error").getAsString().equals("0"))
                return true;
        } catch (Exception e) { }
        return false;
    }

    public static String requestMessageForResetPassword(String phone) {
        String json = new HttpApi(Config.api_address + "/android/reset_passwd_request_message")
                .add("token", Config.request_message_token)
                .add("phone", phone)
                .postForString();
        try {
            JsonObject res = (JsonObject) new JsonParser().parse(json);
            if (res.get("error").getAsString().equals("0"))
                return null;
            return res.get("msg").getAsString();
        } catch (Exception e) { }
        return "网络异常，请稍后再试";
    }

    public static String resetPassword(String phone, String password, String code) {
        String json = new HttpApi(Config.api_address + "/android/reset_passwd")
                .add("phone", phone)
                .add("passwd", password)
                .add("code", code)
                .postForString();
        try {
            JsonObject res = (JsonObject) new JsonParser().parse(json);
            if (res.get("error").getAsString().equals("0"))
                return null;
            return res.get("msg").getAsString();
        } catch (Exception e) { }
        return "网络异常，请稍后再试";
    }

}

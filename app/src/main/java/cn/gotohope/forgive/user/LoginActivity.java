package cn.gotohope.forgive.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.xinsane.util.HttpApi;

import java.util.regex.Pattern;

import cn.gotohope.forgive.App;
import cn.gotohope.forgive.R;
import cn.gotohope.forgive.util.Helper;

public class LoginActivity extends AppCompatActivity {

    private static class MessageHandler extends Handler {

        static final int EVENT_LOGIN_SUCCESS = 0x01;
        static final int EVENT_LOGIN_FAIL = 0x02;

        private AppCompatActivity activity;
        private Button btn_submit;
        MessageHandler(AppCompatActivity activity, Button btn_submit) {
            this.activity = activity;
            this.btn_submit = btn_submit;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_LOGIN_SUCCESS:
                    Toast.makeText(activity, "登陆成功", Toast.LENGTH_LONG).show();
                    activity.setResult(RESULT_OK);
                    activity.finish();
                    break;
                case EVENT_LOGIN_FAIL:
                    Toast.makeText(activity, "登陆失败：" + msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    btn_submit.setText("登陆");
                    btn_submit.setEnabled(true);
                    break;
            }
        }
    }

    private TextInputEditText phone, password;
    private Button btn_submit;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phone = findViewById(R.id.et_mobile);
        password = findViewById(R.id.et_password);
        btn_submit = findViewById(R.id.btn_submit);
        SharedPreferences preferences = App.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String phone_str = preferences.getString("phone", "");
        phone.setText(phone_str);
        handler = new MessageHandler(this, btn_submit);
    }

    public void onRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("phone", phone.getText().toString());
        startActivity(intent);
    }

    public void onResetPassword(View view) {
        Intent intent = new Intent(this, ResetPasswdActivity.class);
        intent.putExtra("phone", phone.getText().toString());
        startActivity(intent);
    }

    public void onLogin(View view) {
        final String account = phone.getText().toString();
        final String passwd = password.getText().toString();
        if (account.isEmpty() || !Pattern.compile("1[0-9]{10}").matcher(account).matches()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("请输入正确的11位手机号").setPositiveButton("确定", null).show();
            return;
        }
        if (passwd.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("密码不能为空").setPositiveButton("确定", null).show();
            return;
        }
        UserManager.login(account, Helper.md5(passwd), new HttpApi.Listener() {
            @Override
            public void onResult(HttpApi.Result result) {
                Message message = new Message();
                if (result.isSuccess())
                    message.what = MessageHandler.EVENT_LOGIN_SUCCESS;
                else {
                    message.what = MessageHandler.EVENT_LOGIN_FAIL;
                    message.obj = result.getMessage();
                }
                handler.sendMessage(message);
            }
            @Override
            public void onFail(String msg) {
                Message message = new Message();
                message.what = MessageHandler.EVENT_LOGIN_FAIL;
                message.obj = msg;
                handler.sendMessage(message);
            }
        });
        btn_submit.setText("登陆中...");
        btn_submit.setEnabled(false);
    }

}

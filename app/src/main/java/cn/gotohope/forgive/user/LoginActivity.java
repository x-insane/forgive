package cn.gotohope.forgive.user;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.regex.Pattern;

import cn.gotohope.forgive.R;
import cn.gotohope.forgive.util.Helper;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText phone, password;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (msg.obj == null) {
                    Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else
                    Toast.makeText(LoginActivity.this, "登陆失败：" + msg.obj.toString(), Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phone = findViewById(R.id.et_mobile);
        password = findViewById(R.id.et_password);
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
        new Thread() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                message.obj = UserManager.login(account, Helper.md5(passwd));
                handler.sendMessage(message);
            }
        }.start();
    }

}

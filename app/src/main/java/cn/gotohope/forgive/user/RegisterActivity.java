package cn.gotohope.forgive.user;

import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.xinsane.util.LogUtil;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import cn.gotohope.forgive.R;
import cn.gotohope.forgive.util.Helper;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText phone, password, captcha;
    private Button btn_captcha, btn_submit;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == -1) {
                Toast.makeText(RegisterActivity.this, "发送验证码成功，请注意查收短信", Toast.LENGTH_SHORT).show();
                btn_submit.setEnabled(true);
                return;
            } else if (msg.what == -2) {
                Toast.makeText(RegisterActivity.this, "发送验证码失败，请检查网络", Toast.LENGTH_SHORT).show();
                return;
            } else if (msg.what == -3) {
                if (msg.obj == null) {
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                    finish();
                } else
                    Toast.makeText(RegisterActivity.this, "注册失败：" + msg.obj.toString(), Toast.LENGTH_SHORT).show();
                return;
            }
            int t = msg.what;
            if (t > 0)
                btn_captcha.setText("获取验证码(" + t + ")");
            else {
                btn_captcha.setText("获取验证码");
                btn_captcha.setEnabled(true);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        phone = findViewById(R.id.et_mobile);
        password = findViewById(R.id.et_password);
        captcha = findViewById(R.id.et_captcha);
        btn_captcha = findViewById(R.id.btn_get_captcha);
        btn_submit = findViewById(R.id.btn_submit);
        phone.setText(getIntent().getStringExtra("phone"));
    }

    public void requestMessage(View view) {
        final String account = phone.getText().toString();
        if (account.isEmpty() || !Pattern.compile("1[0-9]{10}").matcher(account).matches()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("请输入正确的11位手机号").setPositiveButton("确定", null).show();
            return;
        }
        new Thread() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = UserManager.requestMessage(account) ? -1 : -2;
                handler.sendMessage(message);
            }
        }.start();
        btn_captcha.setText("获取验证码(30)");
        btn_captcha.setEnabled(false);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            private int t = 30;
            @Override
            public void run() {
                t --;
                if (t <= 0)
                    cancel();
                Message message = new Message();
                message.what = t;
                handler.sendMessage(message);
            }
        }, 1000, 1000);
    }

    public void onRegister(View view) {
        final String account = phone.getText().toString();
        final String passwd = password.getText().toString();
        final String code = captcha.getText().toString();
        if (account.isEmpty() || !Pattern.compile("1[0-9]{10}").matcher(account).matches()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("请输入正确的11位手机号").setPositiveButton("确定", null).show();
            return;
        }
        if (code.isEmpty() || !Pattern.compile("[0-9]{6}").matcher(code).matches()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("验证码为6位数字").setPositiveButton("确定", null).show();
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
                message.what = -3;
                message.obj = UserManager.register(account, Helper.md5(passwd), code);
                handler.sendMessage(message);
            }
        }.start();
    }
}

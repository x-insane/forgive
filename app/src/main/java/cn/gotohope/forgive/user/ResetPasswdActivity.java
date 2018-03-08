package cn.gotohope.forgive.user;

import android.annotation.SuppressLint;
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

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import cn.gotohope.forgive.R;
import cn.gotohope.forgive.util.Helper;

public class ResetPasswdActivity extends AppCompatActivity {

    private static class MessageHandler extends Handler {

        static final int EVENT_RESET_PASSWORD_SUCCESS = 0x01;
        static final int EVENT_RESET_PASSWORD_FAIL = 0x02;
        static final int EVENT_PHONE_MESSAGE_SUCCESS = 0x03;
        static final int EVENT_PHONE_MESSAGE_FAIL = 0x04;
        static final int EVENT_PHONE_MESSAGE_INTERVAL = 0x05;

        private AppCompatActivity activity;
        private Button btn_submit, btn_captcha;
        MessageHandler(AppCompatActivity activity, Button btn_submit, Button btn_captcha) {
            this.activity = activity;
            this.btn_submit = btn_submit;
            this.btn_captcha = btn_captcha;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_RESET_PASSWORD_SUCCESS:
                    Toast.makeText(activity, "重置密码成功", Toast.LENGTH_LONG).show();
                    activity.finish();
                    break;
                case EVENT_RESET_PASSWORD_FAIL:
                    Toast.makeText(activity, "重置密码失败：" + msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case EVENT_PHONE_MESSAGE_SUCCESS:
                    Toast.makeText(activity, "发送验证码成功，请注意查收短信", Toast.LENGTH_SHORT).show();
                    btn_submit.setEnabled(true);
                    btn_captcha.setText("获取验证码(30)");
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        private int t = 30;
                        @Override
                        public void run() {
                            t --;
                            if (t <= 0)
                                cancel();
                            Message message = new Message();
                            message.what = EVENT_PHONE_MESSAGE_INTERVAL;
                            message.arg1 = t;
                            MessageHandler.this.sendMessage(message);
                        }
                    }, 1000, 1000);
                    break;
                case EVENT_PHONE_MESSAGE_FAIL:
                    Toast.makeText(activity, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    btn_captcha.setText("获取验证码");
                    btn_captcha.setEnabled(true);
                    break;
                case EVENT_PHONE_MESSAGE_INTERVAL:
                    int t = msg.arg1;
                    if (t > 0)
                        btn_captcha.setText("获取验证码(" + t + ")");
                    else {
                        btn_captcha.setText("获取验证码");
                        btn_captcha.setEnabled(true);
                    }
                    break;
            }
        }
    }

    private TextInputEditText phone, password, captcha;
    private Button btn_captcha;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_passwd);
        phone = findViewById(R.id.et_mobile);
        password = findViewById(R.id.et_password);
        captcha = findViewById(R.id.et_captcha);
        btn_captcha = findViewById(R.id.btn_get_captcha);
        Button btn_submit = findViewById(R.id.btn_submit);
        phone.setText(getIntent().getStringExtra("phone"));
        handler = new MessageHandler(this, btn_submit, btn_captcha);
    }

    public void requestMessage(View view) {
        final String account = phone.getText().toString();
        if (account.isEmpty() || !Pattern.compile("1[0-9]{10}").matcher(account).matches()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("请输入正确的11位手机号").setPositiveButton("确定", null).show();
            return;
        }
        UserManager.requestMessage(account, UserManager.TYPE_RESET_PASSWORD, new HttpApi.Listener() {
            @Override
            public void onResult(HttpApi.Result result) {
                Message message = new Message();
                if (result.isSuccess())
                    message.what = MessageHandler.EVENT_PHONE_MESSAGE_SUCCESS;
                else {
                    message.what = MessageHandler.EVENT_PHONE_MESSAGE_FAIL;
                    message.obj = result.getMessage();
                }
                handler.sendMessage(message);
            }
            @Override
            public void onFail(String msg) {
                Message message = new Message();
                message.what = MessageHandler.EVENT_PHONE_MESSAGE_FAIL;
                message.obj = msg;
                handler.sendMessage(message);
            }
        });
        btn_captcha.setText("正在获取验证码");
        btn_captcha.setEnabled(false);
    }

    public void onResetPassword(View view) {
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
        UserManager.resetPassword(account, Helper.md5(passwd), code, new HttpApi.Listener() {
            @Override
            public void onResult(HttpApi.Result result) {
                Message message = new Message();
                if (result.isSuccess())
                    message.what = MessageHandler.EVENT_RESET_PASSWORD_SUCCESS;
                else {
                    message.what = MessageHandler.EVENT_RESET_PASSWORD_FAIL;
                    message.obj = result.getMessage();
                }
                handler.sendMessage(message);
            }
            @Override
            public void onFail(String msg) {
                Message message = new Message();
                message.what = MessageHandler.EVENT_RESET_PASSWORD_FAIL;
                message.obj = msg;
                handler.sendMessage(message);
            }
        });
    }

}

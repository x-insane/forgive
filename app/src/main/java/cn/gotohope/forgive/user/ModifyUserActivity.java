package cn.gotohope.forgive.user;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xinsane.util.HttpApi;

import cn.gotohope.forgive.R;

public class ModifyUserActivity extends AppCompatActivity {

    private TextView textView_account_meta;
    private TextInputEditText et_nickname, et_description;
    private Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user);
        textView_account_meta = findViewById(R.id.text_account_meta);
        et_nickname = findViewById(R.id.et_nickname);
        et_description = findViewById(R.id.et_description);
        btn_submit = findViewById(R.id.btn_submit);
        textView_account_meta.setText("账号：" + UserManager.getUser().phone);
        et_nickname.setText(UserManager.getUser().nickname);
        et_description.setText(UserManager.getUser().description);
//        et_nickname.selectAll();
    }

    public void onSubmit(View view) {
        btn_submit.setEnabled(false);
        UserManager.modifyUser(et_nickname.getText().toString(), et_description.getText().toString(),
            new HttpApi.Listener() {
                @Override
                public void onResult(HttpApi.Result result) {
                    if (result.isSuccess()) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(ModifyUserActivity.this, "修改失败：" + result.getMessage(), Toast.LENGTH_SHORT).show();
                        btn_submit.setEnabled(true);
                    }
                }
                @Override
                public void onFail(String msg) {
                    Toast.makeText(ModifyUserActivity.this, "修改失败：网络错误", Toast.LENGTH_SHORT).show();
                    btn_submit.setEnabled(true);
                }
            });
    }
}

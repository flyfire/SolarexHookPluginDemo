package com.solarexsoft.hookplugindemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.solarexsoft.hookplugindemo.core.HookConstants;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 12:17/2020-02-11
 *    Desc:
 * </pre>
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Intent originIntent;
    EditText et_name;
    EditText et_password;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.btn_submit).setOnClickListener(this);
        et_name = findViewById(R.id.et_name);
        et_password = findViewById(R.id.et_password);
        originIntent = getIntent().getParcelableExtra(HookConstants.KEY_ORIGIN_INTENT);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_submit) {
            if (TextUtils.equals("solarex", et_name.getText().toString()) && TextUtils.equals("123456", et_password.getText().toString())) {
                getSharedPreferences(HookConstants.SP_FILENAME, Context.MODE_PRIVATE).edit().putBoolean(HookConstants.KEY_LOGIN_ALREADY, true).commit();
                if (originIntent != null) {
                    startActivity(originIntent);
                }
                finish();
            } else {
                Toast.makeText(this, "wrong username or password", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

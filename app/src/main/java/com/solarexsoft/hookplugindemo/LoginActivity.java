package com.solarexsoft.hookplugindemo;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 12:17/2020-02-11
 *    Desc:
 * </pre>
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    String jumpToClassName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.btn_submit).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_submit) {

        }
    }
}

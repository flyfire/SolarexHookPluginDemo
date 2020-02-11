package com.solarexsoft.hookplugindemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.solarexsoft.hookplugindemo.core.HookConstants;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_one).setOnClickListener(this);
        findViewById(R.id.btn_two).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_one) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.solarexsoft.plugindemo", "com.solarexsoft.plugindemo.DemoOneActivity"));
            startActivity(intent);
        } else if (id == R.id.btn_two) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.solarexsoft.plugindemo", "com.solarexsoft.plugindemo.DemoTwoActivity"));
            startActivity(intent);
        } else if (id == R.id.btn_login) {
            getSharedPreferences(HookConstants.SP_FILENAME, Context.MODE_PRIVATE).edit().putBoolean(HookConstants.KEY_LOGIN_ALREADY, true).commit();
        } else if (id == R.id.btn_logout) {
            getSharedPreferences(HookConstants.SP_FILENAME, Context.MODE_PRIVATE).edit().putBoolean(HookConstants.KEY_LOGIN_ALREADY, false).commit();
        }
    }
}

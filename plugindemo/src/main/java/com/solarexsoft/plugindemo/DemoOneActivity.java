package com.solarexsoft.plugindemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 21:44/2020-02-11
 *    Desc:
 * </pre>
 */

public class DemoOneActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        findViewById(R.id.btn_two).setOnClickListener(this);
        findViewById(R.id.btn_three).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_two) {
            startActivity(new Intent(this, DemoTwoActivity.class));
        } else if (id == R.id.btn_three) {
            startActivity(new Intent(this, DemoThreeActivity.class));
        }
    }
}

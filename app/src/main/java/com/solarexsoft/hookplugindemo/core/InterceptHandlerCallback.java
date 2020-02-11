package com.solarexsoft.hookplugindemo.core;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.solarexsoft.hookplugindemo.LoginActivity;

import java.lang.reflect.Field;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 13:02/2020-02-11
 *    Desc:
 * </pre>
 */

public class InterceptHandlerCallback implements Handler.Callback {
    private final Handler originMH;
    private final Context context;

    public InterceptHandlerCallback(Handler originMH, Context context) {
        this.originMH = originMH;
        this.context = context;
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {

        // LAUNCH_ACTIVITY
        if (msg.what == 100) {
            fixMsg(msg);
        }
        originMH.handleMessage(msg);
        return true;
    }

    private void fixMsg(Message msg) {
        // ActivityClientRecord
        Object activityClientRecordObj = msg.obj;
        try {
            Field intentField = activityClientRecordObj.getClass().getDeclaredField("intent");
            intentField.setAccessible(true);
            Intent intent = (Intent) intentField.get(activityClientRecordObj);
            Intent originIntent = intent.getParcelableExtra(HookConstants.KEY_ORIGIN_INTENT);
            if (originIntent != null) {
                boolean login = context.getSharedPreferences(HookConstants.SP_FILENAME, Context.MODE_PRIVATE).getBoolean(HookConstants.KEY_LOGIN_ALREADY, false);
                if (login) {
                    intentField.set(activityClientRecordObj, originIntent);
                } else {
                    Intent loginIntent = new Intent(context, LoginActivity.class);
                    loginIntent.putExtra(HookConstants.KEY_ORIGIN_INTENT, originIntent);
                    intentField.set(activityClientRecordObj, loginIntent);
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

package com.solarexsoft.hookplugindemo.core;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.solarexsoft.hookplugindemo.ProxyActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 12:39/2020-02-11
 *    Desc:
 * </pre>
 */

public class StartActivityDelegate implements InvocationHandler {
    private static final String TAG = "StartActivityDelegate";
    private final Object origin;
    private final Context context;

    public StartActivityDelegate(Object origin, Context context) {
        this.origin = origin;
        this.context = context;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Log.d(TAG, "method name = " + methodName);
        if (methodName.equals("startActivity")) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    Intent originIntent = (Intent) args[i];
                    Intent newIntent = new Intent(context, ProxyActivity.class);
                    newIntent.putExtra(HookConstants.KEY_ORIGIN_INTENT, originIntent);
                    args[i] = newIntent;
                    break;
                }
            }
        }
        return method.invoke(origin, args);
    }
}

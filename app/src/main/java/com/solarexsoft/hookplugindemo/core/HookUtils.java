package com.solarexsoft.hookplugindemo.core;

import android.content.Context;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 12:24/2020-02-11
 *    Desc:
 * </pre>
 */

public class HookUtils {

    public static void hookStartActivity(Context context) {
        try {
            Class<?> activityManagerNativeClz = Class.forName("android.app.ActivityManagerNative");
            Field gDefaultField = activityManagerNativeClz.getDeclaredField("gDefault");
            gDefaultField.setAccessible(true);
            Object originGDefault = gDefaultField.get(null);
            Class singletonClz = Class.forName("android.util.Singleton");
            Field mInstanceField = singletonClz.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Object originIActivityManager = mInstanceField.get(originGDefault);

            // 代理
            Class<?> iActivityManagerClz = Class.forName("android.app.IActivityManager");
            Object delegate = Proxy.newProxyInstance(context.getClassLoader(), new Class[]{iActivityManagerClz}, new StartActivityDelegate(originIActivityManager, context));
            mInstanceField.set(originGDefault, delegate);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

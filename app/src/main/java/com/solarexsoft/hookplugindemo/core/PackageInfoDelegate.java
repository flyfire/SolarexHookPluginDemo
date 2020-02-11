package com.solarexsoft.hookplugindemo.core;

import android.content.pm.PackageInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 20:53/2020-02-11
 *    Desc:
 * </pre>
 */

public class PackageInfoDelegate implements InvocationHandler {

    private final Object origin;

    public PackageInfoDelegate(Object origin) {
        this.origin = origin;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (methodName.equals("getPackageInfo")) {
            return new PackageInfo();
        }
        return method.invoke(origin, args);
    }
}

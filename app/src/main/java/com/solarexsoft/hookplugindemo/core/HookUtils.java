package com.solarexsoft.hookplugindemo.core;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.ArrayMap;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import dalvik.system.DexClassLoader;

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

    public static void hookPackageInfo() {
        try {
            Class<?> activityThreadClz = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClz.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);
            Object activityThread = currentActivityThreadMethod.invoke(null);

            Field sPackageManagerField = activityThreadClz.getDeclaredField("sPackageManager");
            sPackageManagerField.setAccessible(true);
            Object originPackageManager = sPackageManagerField.get(activityThread);

            // 代理
            Class<?> iPackageManagerClz = Class.forName("android.content.pm.IPackageManager");
            Object delegatedPackageManager = Proxy.newProxyInstance(activityThreadClz.getClassLoader(), new Class[]{iPackageManagerClz}, new PackageInfoDelegate(originPackageManager));
            sPackageManagerField.set(activityThread, delegatedPackageManager);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void hookLoadedApk(Context context,String apkPath) {
        Class<?> activityThreadClz = null;
        try {
            activityThreadClz = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClz.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);
            Object activityThread = currentActivityThreadMethod.invoke(null);

            Field mPackagesField = activityThreadClz.getDeclaredField("mPackages");
            mPackagesField.setAccessible(true);
            ArrayMap mPackages = (ArrayMap) mPackagesField.get(activityThread);

            // 构造LoadApk
            Class<?> compatibilityInfoClz = Class.forName("android.content.res.CompatibilityInfo");
            Method getPackageInfoNoCheckMethod = activityThreadClz.getDeclaredMethod("getPackageInfoNoCheck", ApplicationInfo.class, compatibilityInfoClz);

            // 构造CompatibilityInfo
            Field defaultCompatibilityInfoField = compatibilityInfoClz.getDeclaredField("DEFAULT_COMPATIBILITY_INFO");
            Object compatibilityInfo = defaultCompatibilityInfoField.get(null);

            // 构造 ApplicationInfo
            ApplicationInfo applicationInfo = parseApplicationInfo(context, apkPath);

            Object loadedApk = getPackageInfoNoCheckMethod.invoke(activityThread, applicationInfo, compatibilityInfo);

            String odexPath = getPluginOptDexDir(context, applicationInfo.packageName).getPath();
            String libPath = getPluginLibDir(context, applicationInfo.packageName).getPath();

            ClassLoader classLoader = new DexClassLoader(apkPath, odexPath, libPath, context.getClassLoader());

            Field mClassLoaderField = loadedApk.getClass().getDeclaredField("mClassLoader");
            mClassLoaderField.setAccessible(true);
            mClassLoaderField.set(loadedApk, classLoader);

            WeakReference weakLoadedApk = new WeakReference(loadedApk);
            mPackages.put(applicationInfo.packageName, weakLoadedApk);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    private static ApplicationInfo parseApplicationInfo(Context context, String apkPath) {
        try {
            Class<?> packageParseClz = Class.forName("android.content.pm.PackageParser");
            Method parsePackageMethod = packageParseClz.getDeclaredMethod("parsePackage", File.class, int.class);
            Object packageParser = packageParseClz.newInstance();
            Object packageObj = parsePackageMethod.invoke(packageParser, new File(apkPath), PackageManager.GET_ACTIVITIES);

            Class<?> packageUserStateClass = Class.forName("android.content.pm.PackageUserState");
            Object defaultUserState= packageUserStateClass.newInstance();
            Method generateApplicationInfoMethod = packageParseClz.getDeclaredMethod("generateApplicationInfo",
                    packageObj.getClass(),
                    int.class,
                    packageUserStateClass);
            ApplicationInfo applicationInfo= (ApplicationInfo) generateApplicationInfoMethod.invoke(packageParser, packageObj, 0, defaultUserState);

            applicationInfo.sourceDir = apkPath;  // resources
            applicationInfo.publicSourceDir = apkPath;
            return applicationInfo;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static File sBaseDir;

    // /data/data/<package>/files/plugin/插件的包名
    private static File getPluginBaseDir(Context context,String packageName) {
        if (sBaseDir == null) {
            sBaseDir = context.getFileStreamPath("plugin");
            enforceDirExists(sBaseDir);
        }
        return enforceDirExists(new File(sBaseDir, packageName));
    }

    private static synchronized File enforceDirExists(File sBaseDir) {
        if (!sBaseDir.exists()) {
            boolean ret = sBaseDir.mkdir();
            if (!ret) {
                throw new RuntimeException("create dir " + sBaseDir + "failed");
            }
        }
        return sBaseDir;
    }

    public static File getPluginOptDexDir(Context context,String packageName) {
        return enforceDirExists(new File(getPluginBaseDir(context, packageName), "odex"));
    }

    public static File getPluginLibDir(Context context,String packageName) {
        return enforceDirExists(new File(getPluginBaseDir(context, packageName), "lib"));
    }
}

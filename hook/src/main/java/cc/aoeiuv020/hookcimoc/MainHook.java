package cc.aoeiuv020.hookcimoc;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.text.TextUtils;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

@SuppressWarnings("RedundantThrows")
public class MainHook implements IXposedHookLoadPackage {
    @SuppressWarnings("All")
    private static final boolean DEBUG = BuildConfig.DEBUG && false;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("handleLoadPackage: " + lpparam.processName + ", " + lpparam.processName);
        XposedHelpers.findAndHookMethod(Instrumentation.class, "callApplicationOnCreate", Application.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (!(param.args[0] instanceof Application)) return;
                hookDebug(lpparam);
                hookPreference(lpparam);
                hookSplash(lpparam);
                hookMain(lpparam);
                hookSearch(lpparam);
                // hookResult(lpparam);
                hookClip(lpparam);
            }
        });

    }

    private void hookPreference(XC_LoadPackage.LoadPackageParam lpparam) {
        var clazz = "com.haleydu.cimoc.manager.PreferenceManager";
        var r = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (!TextUtils.equals((String)param.args[0], "pref_global_shutdown_ad")) {
                    return;
                }
                param.setResult(true);
            }
        };

        XposedHelpers.findAndHookMethod(
                clazz,
                lpparam.classLoader,
                "getBoolean",
                String.class,
                boolean.class,
                r
        );
    }

    private void hookSplash(XC_LoadPackage.LoadPackageParam lpparam) {
        var clazz = "com.haleydu.cimoc.SplashActivity";
        var r = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                log(param);
                XposedHelpers.setObjectField(param.thisObject, "canSkip", true);
                XposedHelpers.callMethod(param.thisObject, "gotoMainActivity");
                param.setResult(null);
            }
        };

        XposedHelpers.findAndHookMethod(
                clazz,
                lpparam.classLoader, "loadSplashAd", r
        );
        XposedHelpers.findAndHookMethod(
                clazz,
                lpparam.classLoader, "showAD", r
        );
    }

    private void log(XC_MethodHook.MethodHookParam param) {
        XposedBridge.log("hook: " + param.thisObject.getClass().getName() + "." + param.method.getName());
        if (DEBUG) {
            XposedBridge.log(new Throwable());
        }
    }

    private void hookMain(XC_LoadPackage.LoadPackageParam lpparam) {
        var clazz = "com.haleydu.cimoc.ui.activity.MainActivity";
        nothing(lpparam, clazz,
                "loadInteractionAd",
                "loadRewardAd",
                "requestInteractionAd",
                "requestRewardAd",
                "showInteractionAd",
                "showRewardAd",
                "startInteractionAd"
        );
    }

    private void hookSearch(XC_LoadPackage.LoadPackageParam lpparam) {
        var clazz = "com.haleydu.cimoc.ui.activity.SearchActivity";
        nothing(lpparam, clazz,
                "initAd",
                "loadBannerAd",
                "requestBannerAd",
                "showAd",
                "showBannerAd"
        );
    }

    private void hookResult(XC_LoadPackage.LoadPackageParam lpparam) {
        var clazz = "com.haleydu.cimoc.source.Kuaikanmanhua";
        var r = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                log(param);
                Object builder = XposedHelpers.newInstance(XposedHelpers.findClass("okhttp3.Request.Builder", lpparam.classLoader));
                XposedHelpers.callMethod(builder, "url", "http://127.0.0.1/");
                Object build = XposedHelpers.callMethod(builder, "build");
                param.setResult(build);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                log(param);
            }
        };

        XposedHelpers.findAndHookMethod(
                clazz,
                lpparam.classLoader,
                "getSearchRequest",
                String.class,
                int.class,
                r
        );
    }

    private void hookClip(XC_LoadPackage.LoadPackageParam lpparam) {
        var clazz = "com.youxiao.ssp.base.tools.n";
        var r = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(null);
            }
        };

        XposedHelpers.findAndHookMethod(
                clazz,
                lpparam.classLoader,
                "a",
                Context.class,
                String.class,
                r
        );
    }

    private void hookDebug(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!DEBUG) {
            return;
        }
        var r = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                // 无功能，必要时断点使用的，
                log(param);
            }
        };

        XposedHelpers.findAndHookMethod(
                "android.app.Activity",
                lpparam.classLoader, "onResume", r
        );
    }

    private void nothing(XC_LoadPackage.LoadPackageParam lpparam, String clazz, String... methods) {
        var r = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                log(param);
                param.setResult(null);
            }
        };
        for (String method : methods) {
            try {
                XposedHelpers.findAndHookMethod(
                        clazz,
                        lpparam.classLoader, method, r
                );
            } catch (Throwable t) {
                XposedBridge.log(t);
            }
        }
    }
}

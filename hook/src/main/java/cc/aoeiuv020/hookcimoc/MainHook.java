package cc.aoeiuv020.hookcimoc;

import android.app.Application;
import android.app.Instrumentation;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

@SuppressWarnings("RedundantThrows")
public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("handleLoadPackage: " + lpparam.processName + ", " + lpparam.processName);
        XposedHelpers.findAndHookMethod(Instrumentation.class, "callApplicationOnCreate", Application.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (!(param.args[0] instanceof Application)) return;
                hookDebug(lpparam);
                hookSplash(lpparam);
                hookMain(lpparam);
                hookSearch(lpparam);
            }
        });

    }

    private void hookSplash(XC_LoadPackage.LoadPackageParam lpparam) {
        var clazz = "com.haleydu.cimoc.SplashActivity";
        nothing(lpparam, clazz, "initAd");
        nothing(lpparam, clazz, "showAD");
    }

    private void hookMain(XC_LoadPackage.LoadPackageParam lpparam) {
        var clazz = "com.haleydu.cimoc.ui.activity.MainActivity";
        nothing(lpparam, clazz, "loadInteractionAd");
        nothing(lpparam, clazz, "loadRewardAd");
        nothing(lpparam, clazz, "requestInteractionAd");
        nothing(lpparam, clazz, "requestRewardAd");
        nothing(lpparam, clazz, "showInteractionAd");
        nothing(lpparam, clazz, "showRewardAd");
        nothing(lpparam, clazz, "startInteractionAd");
    }

    private void hookSearch(XC_LoadPackage.LoadPackageParam lpparam) {
        var clazz = "com.haleydu.cimoc.ui.activity.SearchActivity";
        nothing(lpparam, clazz, "initAd");
        nothing(lpparam, clazz, "loadBannerAd");
        nothing(lpparam, clazz, "requestBannerAd");
        nothing(lpparam, clazz, "showAd");
        nothing(lpparam, clazz, "showBannerAd");
    }

    private void hookDebug(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        var r = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                new Throwable().printStackTrace();
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

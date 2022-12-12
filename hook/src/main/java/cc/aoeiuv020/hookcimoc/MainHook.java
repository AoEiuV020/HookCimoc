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
        var r = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedHelpers.setObjectField(param.thisObject, "canSkip", true);
                XposedHelpers.callMethod(param.thisObject, "gotoMainActivity");
                param.setResult(null);
            }
        };

        XposedHelpers.findAndHookMethod(
                clazz,
                lpparam.classLoader, "initAd", r
        );
/*
        nothing(lpparam, clazz,
                "initAd",
                "showAD");
*/
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

    private void hookDebug(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        var r = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log(new Throwable());
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
                XposedBridge.log(new Throwable());
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

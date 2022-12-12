package cc.aoeiuv020.hookcimoc;

import android.os.Bundle;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

@SuppressWarnings("RedundantThrows")
public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("handleLoadPackage: " + lpparam.processName + ", " + lpparam.processName);
        hookDebug(lpparam);
        hookSplash(lpparam);
        hookMain(lpparam);
        hookSearch(lpparam);

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
    }

    private void hookSearch(XC_LoadPackage.LoadPackageParam lpparam) {
        var clazz = "com.haleydu.cimoc.ui.activity.SearchActivity";
        nothing(lpparam, clazz, "initAd");
        nothing(lpparam, clazz, "showAD");
    }

    private void hookDebug(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        var r = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                new Throwable().printStackTrace();
                super.beforeHookedMethod(param);
            }
        };

        XposedHelpers.findAndHookMethod(
                "android.app.Activity",
                lpparam.classLoader, "onResume", r
        );
    }

    private void nothing(XC_LoadPackage.LoadPackageParam lpparam, String clazz, String... methods) {
        for (String method : methods) {
            XposedHelpers.findAndHookMethod(
                    clazz,
                    lpparam.classLoader, "showAD", XC_MethodReplacement.DO_NOTHING
            );
        }
    }
}

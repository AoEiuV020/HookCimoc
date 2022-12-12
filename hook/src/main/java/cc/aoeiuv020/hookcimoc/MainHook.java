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
        hookSearch(lpparam);

    }

    private void hookSplash(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(
                "com.haleydu.cimoc.SplashActivity",
                lpparam.classLoader, "initAd", XC_MethodReplacement.DO_NOTHING
        );
        XposedHelpers.findAndHookMethod(
                "com.haleydu.cimoc.SplashActivity",
                lpparam.classLoader, "showAD", XC_MethodReplacement.DO_NOTHING
        );
    }

    private void hookSearch(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(
                "com.haleydu.cimoc.ui.activity.SearchActivity",
                lpparam.classLoader, "initAd", XC_MethodReplacement.DO_NOTHING
        );
        XposedHelpers.findAndHookMethod(
                "com.haleydu.cimoc.ui.activity.SearchActivity",
                lpparam.classLoader, "showAD", XC_MethodReplacement.DO_NOTHING
        );
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
}

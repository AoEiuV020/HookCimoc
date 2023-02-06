360加固，用riru-fridainstaller脱壳，  
https://www.52pojie.cn/thread-1658874-1-1.html

```shell
cd /data/local/tmp/finstaller/fs/
echo com.cimoc.haleydu > app.list
cp 安卓4-11脱壳.js.bak ke.js
```

```shell
tar -cf /sdcard/cimocDex.tar *.dex
```

```shell
adb pull /sdcard/cimocDex.tar
unar cimocDex.tar
jadx-gui cimocDex
```

关于某搜索限制，在这里，  
com.haleydu.cimoc.core.Manga.isCopyrightManga，  
具体逻辑是开启广告且6点到21点，屏蔽指定列表中的漫画名的搜索结果，  
挺取巧的，也就是原版半夜也能用，只要在半夜加入书架，白天也能看的，  
这个广告，没有看到开关，是获取判断特定的umid给作者自己免广告，同时免了搜索限制，  
而且只屏蔽特定列表，并不全，也不屏蔽繁体结果，  
[1.7.88版本屏蔽列表](./blockList.txt)
```shell
    public static boolean isCopyrightManga(String str) {
        int i;
        if (!App.getPreferenceManager().getBoolean(PreferenceManager.PREF_GLOBAL_SHUTDOWN_AD, false) && (i = new GregorianCalendar().get(11)) >= 6 && i <= 21) {
            for (String str2 : Constants.blockWordStock) {
                if (str2.equals(str)) {
                    return true;
                }
            }
        }
        return false;
    }
```

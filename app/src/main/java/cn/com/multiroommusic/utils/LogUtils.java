package cn.com.multiroommusic.utils;

import android.util.Log;

/**
 * Created by wang l on 2017/6/2.
 */

public class LogUtils {
    private static int currentLevel = 1;
    public static void i(String tag,String msg){
        if (currentLevel < 3)
            Log.i(tag,msg);
    }

    public static void e(String tag,String msg){
        if (currentLevel < 6)
            Log.i(tag,msg);
    }

    public static void i(String msg){
        i("multiroommusic",msg);
    }
}

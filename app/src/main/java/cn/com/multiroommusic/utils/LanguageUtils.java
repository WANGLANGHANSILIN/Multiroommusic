package cn.com.multiroommusic.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Created by wang l on 2017/6/2.
 */

public class LanguageUtils {
    private static Locale[] languageArr = new Locale[]{Locale.SIMPLIFIED_CHINESE,Locale.TRADITIONAL_CHINESE,Locale.ENGLISH};

    public static void changedLanguage(Context context){
        int i = SPUtil.getSpUtil().getIntValue("positiom");
        LogUtils.i("changedLanguage","i:"+i);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        configuration.setLocale(languageArr[i]);
//        context.createConfigurationContext(configuration);
        resources.updateConfiguration(configuration,displayMetrics);
    }
}

package cn.com.multiroommusic.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wang l on 2017/5/22.
 */

public class SPUtil {
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPreferences;

    private static final SPUtil SP_UTIL = new SPUtil();

    private SPUtil() {
    }

    public static SPUtil getSpUtil(){
        return SP_UTIL;
    }

    public void onCreate(Context context) {
        mPreferences = context.getSharedPreferences("MultiRoomMusic", Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public void putValue(String key,String value){
        mEditor.putString(key,value);
        mEditor.commit();
    }

    public void putValue(String key,int value){
        mEditor.putInt(key,value);
        mEditor.commit();
    }

    public void putValue(String key,boolean value){
        mEditor.putBoolean(key,value);
        mEditor.commit();
    }

    public String getStringValue(String key,String def){
        return mPreferences.getString(key,def);
    }
    public int getIntValue(String key){
        return mPreferences.getInt(key,2);
    }
    public boolean getBooleanValue(String key){
        return mPreferences.getBoolean(key,false);
    }
}

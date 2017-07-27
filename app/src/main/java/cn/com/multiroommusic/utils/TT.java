package cn.com.multiroommusic.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by wang l on 2017/5/23.
 */

public class TT {

    private static Toast sToast;

    public static void showToast(Context context, String msg){
        if (sToast == null)
            sToast = Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        sToast.setText(msg);
        sToast.show();
    }
}

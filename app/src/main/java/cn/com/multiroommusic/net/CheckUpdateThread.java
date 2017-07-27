package cn.com.multiroommusic.net;

import android.content.Context;
import android.content.pm.PackageManager;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import cn.com.multiroommusic.MRMConstant;
import cn.com.multiroommusic.bean.MRMVersionInfoBean;
import cn.com.multiroommusic.utils.AppUpDateUtil;
import cn.com.multiroommusic.utils.DataUitls;

/**
 * Created by wang l on 2017/5/26.
 */

public class CheckUpdateThread extends Thread {

    private Context mContext;
    public CheckUpdateThread(Context context) {
        this.mContext = context;
    }


    @Override
    public void run() {
        super.run();
        try {
            MRMVersionInfoBean serverVersion = getServerVersion();
            DataUitls.newInstance().setAppUpdateVersion(serverVersion.getVersion());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private MRMVersionInfoBean getServerVersion() throws IOException, PackageManager.NameNotFoundException, ParserConfigurationException, SAXException {
        InputStream inputStream = MRMHttp.getInputStream(MRMConstant.UPDATE_URL);
            return AppUpDateUtil.parseXml(inputStream,MRMConstant.APK_TAG, AppUpDateUtil.getAppCurrentVersionName(mContext),1,(byte) 1);
    }
}

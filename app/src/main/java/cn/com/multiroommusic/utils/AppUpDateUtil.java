package cn.com.multiroommusic.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentManager;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import cn.com.multiroommusic.MRMConstant;
import cn.com.multiroommusic.bean.MRMVersionInfoBean;
import cn.com.multiroommusic.dialog.UpDateDialog;

/**
 * Created by wang l on 2017/5/26.
 */

public class AppUpDateUtil {

    public static String getAppCurrentVersionName(Context context) throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        return (packageInfo == null ? null : packageInfo.versionName);
    }

    public static MRMVersionInfoBean parseXml(InputStream in, String tag, String ver, int mode, byte identifier) throws ParserConfigurationException, IOException, SAXException {
        MRMVersionInfoBean versionInfo = new MRMVersionInfoBean();
        NodeList resultNode = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in).getDocumentElement().getElementsByTagName(tag);;

        if (resultNode.getLength() > 0)
        {
            Element tagNode 			= (Element) resultNode.item(0);
            NodeList tagElements 	= tagNode.getElementsByTagName(MRMConstant.TAG_VERSION);
            for (int i = 0; i < tagElements.getLength(); i++)
            {
                Element verElement 	= (Element) tagElements.item(i);

                int 		elementMode	= Integer.parseInt(verElement.getAttribute(MRMConstant.TAG_MODE));
                byte		elementIden	= Byte.parseByte(verElement.getAttribute(MRMConstant.TAG_IDENTIFIER));
                String 	elementName	= verElement.getAttribute(MRMConstant.TAG_FILENAME);
                String	elementVer	= verElement.getFirstChild().getNodeValue();

                if (elementMode == mode && elementIden == identifier)
                {
                    versionInfo.setMode(elementMode);
                    versionInfo.setIdentifier(elementIden);
                    versionInfo.setFileName(elementName);
                    versionInfo.setVersion(elementVer);
                    break;
                }
            }
        }
        return versionInfo;
    }

    public static void checkAppUpgrading(Context context, FragmentManager manager) {
        String appCurrentVersionName = null;
        try {
            appCurrentVersionName = AppUpDateUtil.getAppCurrentVersionName(context);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String serverVersion = DataUitls.newInstance().getAppUpdateVersion();
        LogUtils.i("checkAppUpgrading","serverVersion:"+serverVersion+"   appCurrentVersionName:"+appCurrentVersionName);
        if (serverVersion != null && serverVersion != null && !serverVersion.equals(appCurrentVersionName)) {
            if (serverVersion.compareTo(appCurrentVersionName) > 0) {
                UpDateDialog upDateDialog = new UpDateDialog();
                upDateDialog.show(manager,"updateDialog");
            }
        }
    }
}

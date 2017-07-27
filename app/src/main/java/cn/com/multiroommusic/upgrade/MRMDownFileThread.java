
package cn.com.multiroommusic.upgrade;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import cn.com.multiroommusic.MRMConstant;
import cn.com.multiroommusic.net.MRMHttp;
import cn.com.multiroommusic.utils.DataUitls;
import cn.com.multiroommusic.utils.LogUtils;

public class MRMDownFileThread extends Thread
{
	private Context mContext;
	public MRMDownFileThread(Context context) {
		mContext = context;
	}

	@Override
	public void run() {
		super.run();
		try {
			downFile(MRMConstant.APK_URL);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void downFile(String url) throws IOException {
		FileOutputStream fos = null;
		HttpURLConnection connection = MRMHttp.getConnection(url);
		InputStream content = connection.getInputStream();
		long length = connection.getContentLength();
		if(content != null)
		{
			File file = new File(Environment.getExternalStorageDirectory(), MRMConstant.APK_NAME);
			fos	= new FileOutputStream(file);
			byte[] buf = new byte[1028];
			int ch = -1;
			int count = 0;
			while((ch=content.read(buf)) != -1)
			{
				fos.write(buf, 0, ch);
				count += ch;
				int precent = (int)(((double)count/length)*100);
				DataUitls.newInstance().getDownloadCompleteCallBack().onDownloadComplate(precent);
			}
		}
		fos.flush();
		if (fos != null)
		{
			fos.close();
		}
		finishDownFile();
	}

	private void finishDownFile()
	{
		setup();
	}

	private void setup() {
		File file = new File(Environment.getExternalStorageDirectory(), MRMConstant.APK_NAME);
		LogUtils.i("setup", "SDK:"+Build.VERSION.SDK_INT+",fileName:"+file.getName()+",file:"+file.getAbsolutePath());
		if(Build.VERSION.SDK_INT < 23){
             Intent intents = new Intent();
             intents.setAction("android.intent.action.VIEW");
             intents.addCategory("android.intent.category.DEFAULT");
             intents.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
             intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             mContext.startActivity(intents);
         }else{
             if(file.exists()){
                 openFile(file,mContext);
             }
         }
	}
	
	 private void openFile(File var0, Context var1) {
		Intent var2 = new Intent();
		var2.addFlags(268435456);
		var2.setAction("android.intent.action.VIEW");
		String var3 = getMIMEType(var0);
		var2.setDataAndType(Uri.fromFile(var0), var3);
		try {
			var1.startActivity(var2);
		} catch (Exception var5) {
			var5.printStackTrace();
		}
	 }

	private String getMIMEType(File var0) {

		String var1 = "";
		String var2 = var0.getName();
		String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
		var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
		return var1;
	}
}

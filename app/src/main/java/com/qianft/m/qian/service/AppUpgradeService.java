package com.qianft.m.qian.service;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.qianft.m.qian.R;
import com.qianft.m.qian.activity.MainActivity;
import com.qianft.m.qian.common.Constant;
import com.qianft.m.qian.common.Global;
import com.qianft.m.qian.utils.DownloadUtils;
import com.qianft.m.qian.utils.LogUtil;
import com.qianft.m.qian.utils.MySharePreData;

import java.io.File;

public class AppUpgradeService extends Service {

	public static final int APP_VERSION_LATEST = 0;
	public static final int APP_VERSION_OLDER = 1;

	public static final int mNotificationId = 100;
	private String mDownloadUrl = null;
	private NotificationManager mNotificationManager = null;
	private Notification mNotification = null;
	private PendingIntent mPendingIntent = null;

	private File destDir = null;
	private File destFile = null;

	private static final int DOWNLOAD_FAIL = -1;
	private static final int DOWNLOAD_SUCCESS = 0;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent == null) {
			return Service.START_STICKY_COMPATIBILITY;
		}
		mDownloadUrl = intent.getStringExtra("downloadUrl");
		if (TextUtils.isEmpty(mDownloadUrl)) {
			return Service.START_STICKY_COMPATIBILITY;
		}
		destDir = new File(Environment
				.getExternalStorageDirectory().getPath()
				+ Global.downloadDir);
		//Environment.MEDIA_MOUNTED
		String url = mDownloadUrl;
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
		request.setDescription( "Qianft.apk");
		request.setTitle("钱富通理财");
		// in order for this if to run, you must use the android 3.2 to compile your app
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			request.allowScanningByMediaScanner();
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		}
		request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory().getPath() + "/1aa" + Global.downloadDir,  "Qianft.apk");
		// get download service and enqueue file
		DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		long refernece = manager.enqueue(request);
		MySharePreData.SetLongData(this, "Download", "refernece", refernece);
		return super.onStartCommand(intent, flags, startId);
	}

	public boolean checkApkFile(String apkFilePath) {
		boolean result = false;
		try {
			PackageManager pManager = getPackageManager();
			PackageInfo pInfo = pManager.getPackageArchiveInfo(apkFilePath,
					PackageManager.GET_ACTIVITIES);
			if (pInfo == null) {
				result = false;
			} else {
				result = true;
			}
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}

	public void install(File apkFile) {
		MySharePreData.SetBooleanData(this, Constant.NAVIGATION_SP_NAME + Global.localVersionName, "is_first", true);
		Uri uri = Uri.fromFile(apkFile);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		startActivity(intent);
	}

}

package com.kunpeng.ev.manage;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kunpeng.ev.R;
import com.kunpeng.ev.utils.HttpUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class UpdateManager {

	/* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;

	private String appVersionCode;
	private Context mContext;
	/* 更新进度条 */
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	/* 下载保存路径 */
	private String mSavePath;

	private String downLoadPath;
	private String versionLog;
	private String versionName;

	/* 记录进度条数量 */
	private int progress;

	/* 是否取消更新 */
	private boolean cancelUpdate = false;

	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			// 正在下载
			case DOWNLOAD:
				// 设置进度条位置
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// 安装文件
				installApk();
				break;
			default:
				break;
			}
		};
	};


	public int checkVersion(){
		int VersionCode = getVersionCode(mContext);
		int newVersionCode = VersionCode;
		int minVersionCode = VersionCode;
		//读取最新版本信息
		try {
			JSONObject queryResult = checkVersionQuery();
			newVersionCode = queryResult.getInt("CurrentVersion");
			minVersionCode = queryResult.getInt("MinVersion");
			downLoadPath = queryResult.getString("DownloadPath");
			versionLog = queryResult.getString("VersionLog");
			versionName = queryResult.getString("VersionName");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("VersionCode",VersionCode+"");
		Log.i("minVersionCode",minVersionCode+"");
		//0 表示不需更新， 1表示可以更新 ，2表示强制更新
		if(VersionCode == newVersionCode){
			//showUpdateAvaliable();
			return 0;
		}
		else if(VersionCode >= minVersionCode){
			Log.i("Update",VersionCode+"");
			Log.i("Update",minVersionCode + "");

			Toast.makeText(mContext, "Update" +VersionCode+"", Toast.LENGTH_SHORT).show();
			showUpdateAvaliable();
			return 1;
		}
		else if(VersionCode < minVersionCode){


			//Log.i("Update",minVersionCode + "");
			forceUpdate();
			return 2;
		}else{
			return 0;
		}
	}

	class UpdateRun implements Runnable{
		public void run(){
			showUpdateAvaliable();
		}
	}

	private void forceUpdate() {
		// TODO Auto-generated method stub
		// 构造对话框
		Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_update_title);
		builder.setMessage("您所使用的版本过低，请更新后登录！");
		// 更新
		builder.setPositiveButton(R.string.soft_update_updatebtn, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				// 显示下载对话框
				showDownloadDialog();
			}
		});
		// 稍后更新
		builder.setNegativeButton(R.string.soft_update_later, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}

	private void showUpdateAvaliable() {
		// TODO Auto-generated method stub
		// 构造对话框
				Builder builder = new Builder(mContext);
				builder.setTitle(R.string.soft_update_title);
				builder.setMessage(R.string.soft_update_info);
				// 更新
				builder.setPositiveButton(R.string.soft_update_updatebtn, new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
						// 显示下载对话框
						showDownloadDialog();
					}
				});
				// 稍后更新
				builder.setNegativeButton(R.string.soft_update_later, new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
					}
				});
				Dialog noticeDialog = builder.create();
				noticeDialog.show();
	}

	private JSONObject checkVersionQuery() throws Exception {
		// TODO Auto-generated method stub
		String url = HttpUtil.OLD_URL +"GetVersionCheck" ;
		return new JSONObject(HttpUtil.getRequest(url));
	}

	/**
	 * 获取软件版本号
	 *
	 * @param context
	 * @return
	 */

	private int getVersionCode(Context context)
	{
		int versionCode = 0;
		try
		{
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return versionCode;
	}

	public UpdateManager(Context context)
	{
		this.mContext = context;
	}
	
	/**
	 * 显示软件下载对话框
	 */
	private void showDownloadDialog()
	{
		// 构造软件下载对话框
		Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_updating);
		// 给下载对话框增加进度条
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		builder.setCancelable(false);
		// 取消更新
		builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				// 设置取消状态
				cancelUpdate = true;
			}
		});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		// 现在文件
		downloadApk();
	}

	/**
	 * 下载apk文件
	 */
	private void downloadApk()
	{
		// 启动新线程下载软件
		new downloadApkThread().start();
	}

	/**
	 * 下载文件线程
	 * 
	 *
	 */
	private class downloadApkThread extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					mSavePath = sdpath + "download";
					Log.e("apkPath",mSavePath);
					URL url = new URL(downLoadPath);
					Log.e("downLoadPath",downLoadPath);
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入流
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists())
					{
						file.mkdir();
					}
					File apkFile = new File(mSavePath, versionName);
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do
					{
						int numread = is.read(buf);
						count += numread;
						// 计算进度条位置
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0)
						{
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// 点击取消就停止下载.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			// 取消下载对话框显示
			mDownloadDialog.dismiss();
		}
	};

	/**
	 * 安装APK文件
	 */
	private void installApk()
	{
		File apkfile = new File(mSavePath,versionName);
		if (!apkfile.exists())
		{
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		mContext.startActivity(i);
		//启动APK
		/*
		Intent mIntent = new Intent( );
		ComponentName comp = new ComponentName("com.example.ev", "AppStartAcitivity");    	
		mIntent.setComponent(comp);
		mIntent.setAction("android.intent.action.VIEW");
		mContext.startActivity(mIntent);*/
	}

}

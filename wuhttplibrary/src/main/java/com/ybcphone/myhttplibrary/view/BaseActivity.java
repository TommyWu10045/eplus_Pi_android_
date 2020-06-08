package com.ybcphone.myhttplibrary.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ybcphone.myhttplibrary.R;
import com.ybcphone.myhttplibrary.http.CustomeProgressDialog;
import com.ybcphone.myhttplibrary.http.HttpDownFileListener;
import com.ybcphone.myhttplibrary.http.HttpDownFileResponseListener;
import com.ybcphone.myhttplibrary.http.HttpListener;
import com.ybcphone.myhttplibrary.http.HttpResponseListener;
import com.ybcphone.myhttplibrary.http.ParamsModel;
import com.ybcphone.myhttplibrary.utils.MyBitmap;
import com.ybcphone.myhttplibrary.utils.MyLog;
import com.ybcphone.myhttplibrary.utils.MyUtils;
import com.ybcphone.myhttplibrary.utils.WebAndLocalPathRule;
import com.yanzhenjie.nohttp.BasicBinary;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OnUploadListener;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Locale;

import me.iwf.photopicker.utils.PhotoPickerIntent;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


/**
 * Activity基礎
 * @author forever
 *
 */
public abstract class BaseActivity extends AppCompatActivity

{
	private CustomeProgressDialog m_CustomeProgressDialog = null;
	private boolean m_IsFinish = false;
	public static int mQueueNumber=10;//*** mQueue 请求并发值。




	public  void myShowProgress() {

		if (m_CustomeProgressDialog == null) {
			m_CustomeProgressDialog = new CustomeProgressDialog(this, R.style.CustomDialog);

		}
		m_CustomeProgressDialog.show();
	}
	public  void myHideProgress() {

		if (m_CustomeProgressDialog != null)
			m_CustomeProgressDialog.dismiss();

	}


	public  void myToastString(int resId) {
		MyUtils.toastString(this,getResources().getString(resId));
		//	UIUtils.showToastSafe(resId);

	}

	public  void myToastString(String mess) {
		MyUtils.toastString(this,mess);

	}




	@Override
	protected void onCreate(Bundle savedInstanceState)
	{	
		super.onCreate(savedInstanceState);

		init();
	}
	
	
	@Override
	protected void onDestroy() 
	{			
		super.onDestroy();

		// 和声明周期绑定，退出时取消这个队列中的所有请求，当然可以在你想取消的时候取消也可以，不一定和声明周期绑定。
		mQueue.cancelBySign(object);

		// 因为回调函数持有了activity，所以退出activity时请停止队列。
		mQueue.stop();

	}

	@Override
	protected void onPause() 
	{	
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		m_IsFinish = false;
	}

	@Override
	public void finish()
	{
		super.finish();
		m_IsFinish = true;
	}

	private void init()
	{

		// 初始化请求队列，传入的参数是请求并发值。
		if (mQueue == null) {
			mQueue = NoHttp.newRequestQueue(mQueueNumber);
		}

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}



	//-------------- NoHttp -----------//

	/**
	 * 用来标记取消。
	 */
	private Object object = new Object();

	/**
	 * 请求队列。
	 */
	private RequestQueue mQueue;




	private HttpDownFileListener NoListener = new HttpDownFileListener<Bitmap>() {

		@Override
		public void onSucceed(int what, Response<Bitmap> response, String localFolderPath, String fileName) {
			int responseCode = response.getHeaders().getResponseCode();// 服务器响应码
			if (responseCode == 200) {// 如果确定你们的服务器是get或者post，上面的不用判断
				MyBitmap.saveBitmap2JPG(response.get(), localFolderPath + fileName, 100);
				// setPhoto(localFolderPath + fileName, fileName);
			}
		}

		@Override
		public void onFailed(int what, Response<Bitmap> response) {
			MyLog.e(" =下載遠端圖片失敗 :" + what);
		}
	};


	/**
	 * 发起请求。
	 *
	 * @param what      what.
	 * @param request   请求对象。
	 * @param callback  回调函数。
	 * @param canCancel 是否能被用户取消。
	 * @param isLoading 实现显示加载框。
	 * @param <T>       想请求到的数据类型。
	 */
	public <T> void request(int what,
							Request<T> request,
							HttpListener<T> callback,
							boolean canCancel,
							boolean isLoading) {
		request.setCancelSign(object);
		mQueue.add(what, request, new HttpResponseListener<>(this, request, callback, canCancel, isLoading));
	}




	public boolean noHttp(int what,
						  Request<String> request,
						  boolean canCancel,
						  boolean isLoading) {
		try {
			if(null==mQueue){
				MyLog.e("error  mQueue== null ");
				return false;
			}

			// 初始化请求队列，传入的参数是请求并发值。
			if (mQueue == null) {
				mQueue = NoHttp.newRequestQueue(mQueueNumber);
			}


			String log = "  \n\nHttp 請求：--------------" + what + "------>"
					+ "\nUrl:" + request.url();
			for (String kk : request.getParamKeyValues().keySet()) {
				log = log + "\n" + kk + " : " + request.getParamKeyValues().getValue(kk,0);
			}
			log = log + "\n";//+ "-------------------------------> end";
			MyLog.d(log);


			if (request != null) {
				//  request.setCancelSign(object); //如果先前的請求未完成，取消先前的請求
				mQueue.add(what, request, new HttpResponseListener<>(this, request, httpListener, canCancel, isLoading));

				MyLog.e("11111 ");

				return true;
			} else {
				return false;
			}


		} catch (Exception e) {
			MyLog.e("error  noHttp: " + e.toString());
			return false;

		}
	}


	private HttpListener<String> httpListener = new HttpListener<String>() {

		@Override
		public void onSucceed(int what, Response<String> response) {
			if (response.getHeaders().getResponseCode() == 501) {
				//myToastString(R.string.request_method_patch);
				MyLog.e("Error 501 Result: 服務器不支持的請求方法。");
				//  showMessageDialog(R.string.request_succeed, );
			} else if (RequestMethod.HEAD == response.request().getRequestMethod())// 请求方法为HEAD时没有响应内容
			{
				// myToastString(R.string.request_method_head);
				MyLog.e("Error 501 Result: 請求方法為HEAD，沒有響應內容。 ");
			} else if (response.getHeaders().getResponseCode() == 405) {
				List<String> allowList = response.getHeaders().getValues("Allow");
				String allow = getString(R.string.request_method_not_allow);
				if (allowList != null && allowList.size() > 0) {
					allow = String.format(Locale.getDefault(), allow, allowList.get(0));
				}
				MyLog.d("OK HTTPS Result: =========>\n" + allow);
			} else {
				onNoHttpDataFinish(what, response.get());
			}


		}

		@Override
		public void onFailed(int what, Response<String> response) {
			MyUtils.toastString(getApplicationContext(), response.getException().getMessage());
		}
	};


	public void onNoHttpDataFinish(int what, String result) {

	}


	public String noHttpResponse(Response<String> response) {
		String result = "";
		if (response.getHeaders().getResponseCode() == 501) {
			//myToastString(R.string.request_method_patch);
			MyLog.e("Error 501 Result: 服務器不支持的請求方法。");
			//  showMessageDialog(R.string.request_succeed, );
		} else if (RequestMethod.HEAD == response.request().getRequestMethod())// 请求方法为HEAD时没有响应内容
		{
			// myToastString(R.string.request_method_head);
			MyLog.e("Error 501 Result: 請求方法為HEAD，沒有響應內容。 ");
		} else if (response.getHeaders().getResponseCode() == 405) {
			List<String> allowList = response.getHeaders().getValues("Allow");
			String allow = getString(R.string.request_method_not_allow);
			if (allowList != null && allowList.size() > 0) {
				allow = String.format(Locale.getDefault(), allow, allowList.get(0));
			}
			MyLog.d("OK HTTPS Result: =========>\n" + allow);
		} else {
			result = response.get();
		}
		return result;
	}


	private final int WHAT_UPLOAD_SINGLE = 0x01;

	/**
	 * 上传单个文件。
	 */
	public void noHttpSingleFile(int what, Request<String> request, String keyName, String filePath) {


		BasicBinary binary = new FileBinary(new File(filePath));
		/**
		 * 监听上传过程，如果不需要监听就不用设置。
		 * 第一个参数：what，what和handler的what一样，会在回调被调用的回调你开发者，作用是一个Listener可以监听多个文件的上传状态。
		 * 第二个参数： 监听器。
		 */
		binary.setUploadListener(WHAT_UPLOAD_SINGLE, mOnUploadListener);

		String log = "  \n\nHttp 請求上傳圖片：--------------" + what + "------>"
				+ "\npicUrl:" + request.url();
		for (String kk : request.getParamKeyValues().keySet()) {
			log = log + "\n" + kk + " : " + request.getParamKeyValues().getValue(kk,0);
		}
		log = log + "\nkeyName：" + keyName + "\n";//+ "-------------------------------> end";
		log = log + "\n";//+ "-------------------------------> end";
		MyLog.d(log);

		request.add(keyName, binary);// 添加1个文件
		// request.add("Photo", binary);// 添加1个文件
//      request.add("image1", fileBinary1);// 添加2个文件


		request(what, request, new HttpListener<String>() {
			@Override
			public void onSucceed(int what, Response<String> response) {
				//    MyLog.d("--onSucceed->" + response.get());
				onNoHttpDataFinish(what, response.get());
				// showMessageDialog(R.string.request_succeed, response.get());
			}

			@Override
			public void onFailed(int what, Response<String> response) {
				//  MyLog.d("--onFailed->" + response.getException().getMessage());
				onNoHttpDataFinish(what, response.getException().getMessage());
				// showMessageDialog(R.string.request_failed, response.getException().getMessage());
			}
		}, false, true);
	}


	/**
	 * 上传单个文件。
	 */
	public void uploadSingleFile2(String url, String keyName, String filePath, List<ParamsModel> params) {
		MyLog.d("picUrl:" + url + "\n filePath:" + filePath);

		// Request<String> request = NoHttp.createStringRequest("https://www.happyhair.com.tw/v1/userdata/UserPhotoUpload", RequestMethod.POST);
		Request<String> request = NoHttp.createStringRequest(url, RequestMethod.POST);
		// 添加普通参数。


		for (ParamsModel pp : params) {
			// request.add("UserID", "2b1b737853d743788fc44c0eb0bf33ec");
			request.add(pp.name, pp.value);
		}


		// 上传文件需要实现NoHttp的Binary接口，NoHttp默认实现了FileBinary、InputStreamBinary、ByteArrayBitnary、BitmapBinary。

		// FileBinary用法
		// String filePath = AppConfig.getInstance().APP_PATH_ROOT + "/image1.jpg";
		BasicBinary binary = new FileBinary(new File(filePath));

		/**
		 * 监听上传过程，如果不需要监听就不用设置。
		 * 第一个参数：what，what和handler的what一样，会在回调被调用的回调你开发者，作用是一个Listener可以监听多个文件的上传状态。
		 * 第二个参数： 监听器。
		 */
		binary.setUploadListener(WHAT_UPLOAD_SINGLE, mOnUploadListener);

		request.add(keyName, binary);// 添加1个文件
		// request.add("Photo", binary);// 添加1个文件
//      request.add("image1", fileBinary1);// 添加2个文件

		request(0, request, new HttpListener<String>() {
			@Override
			public void onSucceed(int what, Response<String> response) {
				MyLog.d("--onSucceed->" + response.get());
				onNoHttpDataFinish(what, response.get());
				// showMessageDialog(R.string.request_succeed, response.get());
			}

			@Override
			public void onFailed(int what, Response<String> response) {
				MyLog.d("--onFailed->" + response.getException().getMessage());
				onNoHttpDataFinish(what, response.getException().getMessage());
				// showMessageDialog(R.string.request_failed, response.getException().getMessage());
			}
		}, false, true);
	}


	/**
	 * 文件上传监听。
	 */
	private OnUploadListener mOnUploadListener = new OnUploadListener() {

		@Override
		public void onStart(int what) {// 这个文件开始上传。
			//  mTvResult.setText(R.string.upload_start);
		}

		@Override
		public void onCancel(int what) {// 这个文件的上传被取消时。
			// mTvResult.setText(R.string.upload_cancel);
		}

		@Override
		public void onProgress(int what, int progress) {// 这个文件的上传进度发生边耍
			// mPbProgress.setProgress(progress);
		}

		@Override
		public void onFinish(int what) {// 文件上传完成
			// mTvResult.setText(R.string.upload_succeed);
		}

		@Override
		public void onError(int what, Exception exception) {// 文件上传发生错误。
			// mTvResult.setText(R.string.upload_error);
		}
	};


	public String erroJsonDesc(String jsonStr) {
		String Desc = "";
		try {
			JSONObject jSONObject = new JSONObject(jsonStr);
			Desc = jSONObject.getString("Desc");
		} catch (Exception e) {
			MyLog.e("--erroJson-> Error:" + e.toString());
		}

		return Desc;
	}

	public String erroJsonCode(String jsonStr) {
		String Errorcode = "999";
		try {
			JSONObject jSONObject = new JSONObject(jsonStr);
			Errorcode = jSONObject.getString("Errorcode");
		} catch (Exception e) {
			MyLog.e("--erroJson-> Error:" + e.toString());
		}

		return Errorcode;
	}


	public void pickPhoto(int reqCode, int num) {
		PhotoPickerIntent intent = new PhotoPickerIntent(this);
		intent.setPhotoCount(num);
		intent.setShowCamera(true);
		startActivityForResult(intent, reqCode);
	}


	public void compressPhoto(Context context, String fileName, final int requestCode) {
		MyLog.d("compressPhoto 開始壓圖片：" + fileName);

		File oldFile = new File(fileName);
		String getPath = WebAndLocalPathRule.getLocaLPath(this, WebAndLocalPathRule.FOLDER_UPLOAD_TEMP);
		MyLog.d("getPath：" + getPath);
		Luban.with(context)
				.load(oldFile)                                   // 传人要压缩的图片列表
				.ignoreBy(100)                                  // 忽略不压缩图片的大小
				.setTargetDir(getPath)                        // 设置压缩后文件存储位置
				.setCompressListener(new OnCompressListener() { //设置回调
					@Override
					public void onStart() {
						MyLog.d("compressPhoto onStart");
					}

					@Override
					public void onSuccess(File file) {
						String pickCompressfile = file.getAbsolutePath();
						MyLog.d("compressPhoto onSuccess：" + pickCompressfile);
						onCompressPhotoFinish(requestCode, pickCompressfile);
					}

					@Override
					public void onError(Throwable e) {
						MyLog.e("compressPhoto onFail");
					}
				}).launch();    //启动压缩

	}

	public void onCompressPhotoFinish(int requestCode, String fileName) {

	}
	/**
	 * 压缩图片 Listener 方式
	 */
	public void compressPhoto(final List<String> photos, final int requestCode) {
		String getPath = WebAndLocalPathRule.getLocaLPath(this, WebAndLocalPathRule.FOLDER_UPLOAD_TEMP);
		Luban.with(this)
				.load(photos)
				.ignoreBy(100)
				.setTargetDir(getPath)
				.setCompressListener(new OnCompressListener() {
					@Override
					public void onStart() {
					}

					@Override
					public void onSuccess(File file) {
						onCompressPhotoFinish(requestCode, file.getAbsolutePath());
						// showResult(photos, file);
					}

					@Override
					public void onError(Throwable e) {
					}
				}).launch();
	}


}

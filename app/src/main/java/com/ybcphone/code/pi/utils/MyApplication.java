package com.ybcphone.code.pi.utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yanzhenjie.nohttp.NoHttp;
import com.ybcphone.myhttplibrary.utils.MyLog;
import com.ybcphone.myhttplibrary.utils.MyUtils;
import com.ybcphone.myhttplibrary.utils.VeDate;
import com.ybcphone.myhttplibrary.utils.WebAndLocalPathRule;
import com.ybcphone.myhttplibrary.view.ScrollWebView;
//import com.yanzhenjie.nohttp.BuildConfig;
//import com.yanzhenjie.nohttp.Logger;
//import com.yanzhenjie.nohttp.NoHttp;


public class MyApplication extends Application {


    //public static  String PHP_HOST = "";


    protected static MyApplication mInstance;
    public static boolean testNameMode = true;
    public static boolean isLogin = false;

    public static String VERSION = "";

    public static String DEVICE_ID = "";
    public static String PUSH_TOKEN = "";


    private DisplayMetrics displayMetrics = null;
    public static String localFolderPath = "";

    public static String ThisTime = "";//手機發生時間
    public static int ThisTimeCount = 0;//手機發生時間
    public static int mQueueNumber = 10;//*** mQueue 请求并发值。
    public static int http_PageNum = 30;//每次分頁資料數
    public static int DisplayMetrics_widthPixels;
    public static int DisplayMetrics_heightPixels;



    public MyApplication() {
        mInstance = this;
    }

    @Override
    public void onCreate() {
        MyLog.v("=================MyApplication=====================");

        super.onCreate();

        VERSION = MyUtils.getVerSion(getApplicationContext());
        DEVICE_ID = MyUtils.getAndroidID(getApplicationContext());

        MyLog.d("==DEVICE_ID:" + DEVICE_ID);
        MyLog.d("==VERSION:" + VERSION);


        //  initImageLoader();
        mInstance = this;

        DisplayMetrics m_DisplayMetrics = MyUtils.getDisplayMetrics(getApplicationContext());
        DisplayMetrics_widthPixels = m_DisplayMetrics.widthPixels;
        DisplayMetrics_heightPixels = m_DisplayMetrics.heightPixels;
        //   ScreenHeight = m_DisplayMetrics.heightPixels;

        MyLog.d("---ScreenWidth - heightPixels: " + DisplayMetrics_heightPixels + "    viewWidth:" + DisplayMetrics_widthPixels);
        localFolderPath = WebAndLocalPathRule.getLocaLPath(this, WebAndLocalPathRule.PICTURE_FOLDER);


        //  LeakCanary.install(this);


        //  Logger.setDebug(BuildConfig.DEBUG);// 开启NoHttp的调试模式, 配置后可看到请求过程、日志和错误信息。
        //  Logger.setTag("totur");// 设置NoHttp打印Log的tag。

        // 一般情况下你只需要这样初始化：
        NoHttp.initialize(this);
        //   Headers.HEAD_VALUE_CONTENT_TYPE_OCTET_STREAM

        // 如果你需要自定义配置：
    /*    NoHttp.initialize(InitializationConfig.newBuilder(this)
                // 设置全局连接超时时间，单位毫秒，默认10s。
                .connectionTimeout(60 * 1000)
                // 设置全局服务器响应超时时间，单位毫秒，默认10s。
                .readTimeout(60 * 1000)
                // 配置缓存，默认保存数据库DBCacheStore，保存到SD卡使用DiskCacheStore。
                .cacheStore(
                        new DBCacheStore(this).setEnable(false) // 如果不使用缓存，设置setEnable(false)禁用。
                )
                // 配置Cookie，默认保存数据库DBCookieStore，开发者可以自己实现。
                .cookieStore(
                        new DBCookieStore(this).setEnable(false) // 如果不维护cookie，设置false禁用。
                )
                // 配置网络层，URLConnectionNetworkExecutor，如果想用OkHttp：OkHttpNetworkExecutor。
                .networkExecutor(new URLConnectionNetworkExecutor())
                .build()
        );*/

        // 如果你需要用OkHttp，请依赖下面的项目，version表示版本号：
        // compile 'com.yanzhenjie.nohttp:okhttp:1.1.1'

        // NoHttp详细使用文档：http://doc.nohttp.net
    /*    new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    ThisTimeCount++;

                    MyLog.d("999ThisTimeCount:" + ThisTimeCount);
                    MyLog.d("9999ThisTime:" + ThisTime);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
        initPhotoError();
    }


    public static void initPhotoError() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }


    }

    public static boolean checkDebugMyDevice() {
        boolean vv = (
                MyApplication.DEVICE_ID.equals("d85d3895271fcc39") ||
                        MyApplication.DEVICE_ID.equals("d7676c6e3734d98e"));
        return vv;
    }

    public static WebSettings setCommWebVewSet(ScrollWebView sv, String content) {

        WebSettings webSettings = sv.getSettings();
        webSettings.setJavaScriptEnabled(true);    // 设置WebView属性，能够执行Javascript脚本
        webSettings.setBuiltInZoomControls(false); //设置支持缩放
        webSettings.setSupportZoom(false);  //支持缩放
        /*
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);   //支持内容重新布局
        webSettings.supportMultipleWindows();   //多窗口
        webSettings.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true);    //支持自动加载图片
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);    //提高渲染的优先级
        webSettings.setAppCacheEnabled(true);// 开启H5(APPCache)缓存功能
        webSettings.setDomStorageEnabled(true); // 开启 DOM storage 功能
        webSettings.setDatabaseEnabled(true);  // 应用可以有数据库
        webSettings.setAllowFileAccess(true); // 可以读取文件缓存(manifest生效)
        */


        sv.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
        sv.setWebViewClient(new WebViewClient());
        sv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }


        });

        return webSettings;
    }

    public static void clearAccount_logout(Context cc) {
        saveAccount(cc);
        //     MyUtils.saveSharedPreferencesData(cc, Consts.Shared_KEY_usermodel_cusid, MyApplication.userModel.cusid);
        //   MyUtils.saveSharedPreferencesData(cc, Consts.Shared_KEY_usermodel_choseKind, MyApplication.userModel.choseKind);
    }

    public static void saveAccount(Context cc) {

    }



    public int dp2px(float f) {
        return (int) (0.5F + f * getScreenDensity());
    }

    public float getScreenDensity() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.density;
    }

    public int getScreenHeight() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.heightPixels;
    }

    public int getScreenWidth() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.widthPixels;
    }

    public void setDisplayMetrics(DisplayMetrics DisplayMetrics) {
        this.displayMetrics = DisplayMetrics;
    }

    public static MyApplication getApp() {
        if (mInstance != null && mInstance instanceof MyApplication) {
            return (MyApplication) mInstance;
        } else {
            mInstance = new MyApplication();
            mInstance.onCreate();
            return (MyApplication) mInstance;
        }
    }


    public static void mySendBroadcast(Context c, int type) {

        //  MyLog.v("  mySendBroadcast= " + type);
        Intent intent = new Intent(Consts.BROADCAST_MESSAGE_intentFilter);
        intent.putExtra(Consts.BROADCAST_MESSAGE_KEY_DATATPYE_KEY, type);
        c.sendBroadcast(intent);
    }


    public static void mySendBroadcast(Context c, int type, String data) {

        MyLog.v("  mySendBroadcast= " + type);
        Intent intent = new Intent(Consts.BROADCAST_MESSAGE_intentFilter);
        intent.putExtra(Consts.BROADCAST_MESSAGE_KEY_DATATPYE_KEY, type);
        intent.putExtra(Consts.BROADCAST_MESSAGE_KEY_DATATPYE_DATA, data);
        c.sendBroadcast(intent);
    }

    public static void mySendBroadcastProgress(Context c, int type, int progress) {

        MyLog.v("  mySendBroadcast= " + type);
        Intent intent = new Intent(Consts.BROADCAST_MESSAGE_intentFilter);
        intent.putExtra(Consts.BROADCAST_MESSAGE_KEY_DATATPYE_KEY, type);
        intent.putExtra(Consts.BROADCAST_MESSAGE_KEY_DATATPYE_DATA, progress);
        c.sendBroadcast(intent);
    }

    public static void myDelayTime(int time) {
        while (time > 0) {
            time--;
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static String getCountDownTime(Context cc, String date) {
        if (date.length() > 0) {
            try {
                String nowTime = VeDate.getPreTimeSec(MyApplication.ThisTime, MyApplication.ThisTimeCount + "");//依照Server上，算出的當前時間
                // MyLog.d("  ThisTime: "+ MyApplication.ThisTime);
                // MyLog.d("  ThisTimeCount: "+ MyApplication.ThisTimeCount);
                //  MyLog.d("  算出的當前時間: "+nowTime);
                //  MyLog.d("  結束時間: "+date);
                date = date.replace("\\/", "-");
                date = date.replace("/", "-");
                return VeDate.getExiTrackStrDay(nowTime, date,
                        cc.getResources().getString(com.ybcphone.myhttplibrary.R.string.text_tian),
                        cc.getResources().getString(com.ybcphone.myhttplibrary.R.string.text_shi),
                        cc.getResources().getString(com.ybcphone.myhttplibrary.R.string.text_fen),
                        cc.getResources().getString(com.ybcphone.myhttplibrary.R.string.text_miao));

            } catch (Exception e) {
                MyLog.d("  getLoveTimeStr= 時間格式錯誤 ");
                return "";
            }
        }
        return "";
    }

}

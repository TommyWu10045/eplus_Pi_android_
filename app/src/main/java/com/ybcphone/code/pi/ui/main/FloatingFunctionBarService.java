package com.ybcphone.code.pi.ui.main;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ybcphone.code.pi.R;
import com.ybcphone.code.pi.utils.Consts;
import com.ybcphone.myhttplibrary.utils.MyLog;
import com.ybcphone.myhttplibrary.utils.MyUtils;
import com.ybcphone.myhttplibrary.view.ViewHeadPic;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;


public class FloatingFunctionBarService extends Service {

    private boolean threadDisable = false;
    private boolean isViewShow = true;
    private int isViewShow_time = 0;
    private float v_speed = 0;
    private float v_incline = 0;
    private MsgReceiver msgReceiver;

    private WindowManager mWindowManager;
    private View mOverlayView;
    private WindowManager.LayoutParams params;

    private TextView tv_incline_value;
    private TextView tv_speed_value;
    private Button btn_speed_down;
    private Button btn_speed_up;
    private Button btn_incline_down;
    private Button btn_incline_up;

    private ImageButton btn_cool_down;
    private ImageButton btn_pause;
    private ImageButton btn_fan;
    private ImageButton btn_stop;
    private ImageButton activity_sport_close;


    private RelativeLayout activity_sport;

    boolean activity_background;


    //----------WeakReference Handler
    private static class MyHandler extends Handler {
        private final WeakReference<FloatingFunctionBarService> finalWeakObjct;

        public MyHandler(FloatingFunctionBarService fromObject) {
            finalWeakObjct = new WeakReference<>(fromObject);
        }

        @Override
        public void handleMessage(Message msg) {
            FloatingFunctionBarService thisObject = finalWeakObjct.get();
            if (thisObject != null) {
                //    —>   thisObject.

                thisObject.showView();

            }
        }
    }

    private final MyHandler myHandler = new MyHandler(this);
//----------WeakReference Handler--- end


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            activity_background = intent.getBooleanExtra("activity_background", false);

        }

        if (mOverlayView == null) {
            mOverlayView = LayoutInflater.from(this).inflate(R.layout.overlay_floating_function_bar, null);


            int layoutFloatType = WindowManager.LayoutParams.TYPE_PHONE;
            // layoutFloatType =  WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

            MyLog.d("Build.VERSION.SDK_INT:" + Build.VERSION.SDK_INT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutFloatType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            }
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    layoutFloatType,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);


            params.gravity = Gravity.BOTTOM;
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mOverlayView, params);

            Display display = mWindowManager.getDefaultDisplay();
            final Point size = new Point();
            display.getSize(size);

            tv_incline_value = mOverlayView.findViewById(R.id.tv_incline_value);
            tv_speed_value = mOverlayView.findViewById(R.id.tv_speed_value);

            tv_speed_value.setText(v_speed + "");
            tv_incline_value.setText(v_incline + "");


            activity_sport_close = mOverlayView.findViewById(R.id.activity_sport_close);
            btn_speed_down = mOverlayView.findViewById(R.id.btn_speed_down);
            btn_speed_up = mOverlayView.findViewById(R.id.btn_speed_up);
            btn_incline_down = mOverlayView.findViewById(R.id.btn_incline_down);
            btn_incline_up = mOverlayView.findViewById(R.id.btn_incline_up);

            btn_cool_down = mOverlayView.findViewById(R.id.btn_cool_down);


            btn_pause = mOverlayView.findViewById(R.id.btn_pause);
            btn_fan = mOverlayView.findViewById(R.id.btn_fan);
            btn_stop = mOverlayView.findViewById(R.id.btn_stop);

            activity_sport_close.setOnClickListener(m_Click_ProudcutListener);
            btn_speed_down.setOnClickListener(m_Click_ProudcutListener);
            btn_speed_up.setOnClickListener(m_Click_ProudcutListener);
            btn_incline_down.setOnClickListener(m_Click_ProudcutListener);
            btn_incline_up.setOnClickListener(m_Click_ProudcutListener);

            btn_cool_down.setOnClickListener(m_Click_ProudcutListener);
            btn_pause.setOnClickListener(m_Click_ProudcutListener);
            btn_fan.setOnClickListener(m_Click_ProudcutListener);
            btn_stop.setOnClickListener(m_Click_ProudcutListener);


            activity_sport = mOverlayView.findViewById(R.id.activity_sport);
            isViewShow = true;
            myHandler.sendEmptyMessage(0);


        } else {

            //    counterFab.increase();

        }


        return super.onStartCommand(intent, flags, startId);


    }


    private void showView() {


        if (isViewShow) {
            activity_sport.setVisibility(View.VISIBLE);
        } else {
            activity_sport.setVisibility(View.GONE);
        }
        tv_speed_value.setText(MyUtils.flaot2str1(v_speed));
        tv_incline_value.setText(MyUtils.flaot2str1(v_incline));

    }


    private void closeAPP(String pagename) {


        try
        {
            Process suProcess = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());

            os.writeBytes("adb shell" + "\n");
            os.flush();

            Context newContext=this;
            ActivityManager activityManager = (ActivityManager) newContext.getSystemService( Context.ACTIVITY_SERVICE );
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            for(ActivityManager.RunningAppProcessInfo appProcess : appProcesses){
//
//                if(appProcess.processName.equals(Consts.PACKAGE_NAME_eplus) ||appProcess.processName.equals(Consts.PACKAGE_NAME_my)
//                        ){
//
//                }
//                else{
                os.writeBytes("am force-stop "+pagename + "\n");
                //  os.writeBytes("am force-stop "+appProcess.processName + "\n");
//                }
            }

            os.flush();
            os.close();
            suProcess.waitFor();

        }

        catch (IOException ex)
        {
            ex.getMessage();
            Toast.makeText(getApplicationContext(), ex.getMessage(),Toast.LENGTH_LONG).show();
        }
        catch (SecurityException ex)
        {
            Toast.makeText(getApplicationContext(), "Can't get root access2",
                    Toast.LENGTH_LONG).show();
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), "Can't get root access3",
                    Toast.LENGTH_LONG).show();
        }

//
//        try {
//            //關閉其他應用
//            Process exec = Runtime.getRuntime().exec("am force-stop " + pagename);
//            //打開其他應用
//            //   Process exec = Runtime.getRuntime().exec("adb shell am start -n 包名/啓動類名稱");
//            if (exec.waitFor() == 0) {
//                //執行成功
//                MyLog.d("closeAPP " + pagename + "  執行成功  ");
//            } else {
//                MyLog.e("closeAPP " + pagename + "  error  ");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private View.OnClickListener m_Click_ProudcutListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            isViewShow_time = 0;


            switch (v.getId()) {
                case R.id.activity_sport_close:


                    closeAPP(Consts.PACKAGE_NAME_spotify);
                    closeAPP(Consts.PACKAGE_NAME_netflix);

                    isViewShow = false;
                    myHandler.sendEmptyMessage(0);

                    try {
                        Intent intent =
                                getPackageManager()
                                        .getLaunchIntentForPackage(
                                                Consts.PACKAGE_NAME_my);
                        getApplicationContext().startActivity(intent);

                    } catch (Exception e) {

                    }

                    break;
                case R.id.btn_speed_down:
                    v_speed -= 0.1;
                    if (v_speed < 0) {
                        v_speed = 0;
                    }

                    break;

                case R.id.btn_speed_up:
                    v_speed += 0.1;
                    if (v_speed > 25) {
                        v_speed = 25;
                    }
                    break;


                case R.id.btn_incline_down:
                    v_incline -= 0.5;
                    if (v_incline < 0) {
                        v_incline = 0;
                    }
                    break;


                case R.id.btn_incline_up:
                    v_incline += 0.5;
                    if (v_incline > 100) {
                        v_incline = 100;
                    }
                    break;


                case R.id.btn_cool_down:

                    break;


                case R.id.btn_pause:

                    break;


                case R.id.btn_fan:

                    break;

                case R.id.tv_btn_fan:

                    break;

                case R.id.btn_stop:

                    break;


            }
            myHandler.sendEmptyMessage(0);


        }
    };


    @Override
    public void onCreate() {
        super.onCreate();

        setTheme(R.style.AppTheme);
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Consts.BROADCAST_MESSAGE_intentFilter);
        registerReceiver(msgReceiver, intentFilter);


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!threadDisable) {

                    isViewShow_time++;
                    if (isViewShow_time > 5) {
                        isViewShow_time = 0;
                        if (isViewShow) {
                            isViewShow = false;
                            myHandler.sendEmptyMessage(0);
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
            }
        }).start();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        threadDisable = true;
        if (mOverlayView != null)
            mWindowManager.removeView(mOverlayView);

        unregisterReceiver(msgReceiver);
    }


    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int dataType = intent.getIntExtra(Consts.BROADCAST_MESSAGE_KEY_DATATPYE_KEY, 0);
            MyLog.d("-------onReceive:" + dataType);
            switch (dataType) {
                case Consts.EPLUS_SHOW_FUNCTION_BAR:

                    isViewShow = true;
                    isViewShow_time = 0;
                    myHandler.sendEmptyMessage(0);
                    break;


                case Consts.SERVER_dataType_push_new_stop:


                    //  showData();

                    break;


            }


        }

    }

}
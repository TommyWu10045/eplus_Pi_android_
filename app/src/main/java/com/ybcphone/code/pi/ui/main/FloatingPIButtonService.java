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
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ybcphone.code.pi.R;
import com.ybcphone.code.pi.utils.Consts;
import com.ybcphone.code.pi.utils.MyApplication;
import com.ybcphone.myhttplibrary.utils.MyLog;
import com.ybcphone.myhttplibrary.utils.MyUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;


/**
 * Created by anupamchugh on 01/08/17.
 */

public class FloatingPIButtonService extends Service {

    private MsgReceiver msgReceiver;

    private WindowManager mWindowManager;
    private View mOverlayView;
    private WindowManager.LayoutParams params;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;

    private int mWidth;
    private RelativeLayout over_layout_all;
    private ImageButton overlay_floating_pi_button_pi_button;
    private ImageView overlay_floating_pi_button_circle;
    private ImageView overlay_floating_pi_button_circle2;


    private Button overlay_floating_pi_button_circle_btn_eplus;
    private Button overlay_floating_pi_button_circle_btn_nfx;
    private Button overlay_floating_pi_button_circle_btn_spy;
    private Button overlay_floating_pi_button_circle_btn_end;
    boolean activity_background;


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
            mOverlayView = LayoutInflater.from(this).inflate(R.layout.overlay_floating_pi_button, null);

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

            //Specify the view position
            params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
            params.width = MyUtils.dp2px(getApplicationContext(), 60);
            params.height = MyUtils.dp2px(getApplicationContext(), 60);
            params.x = 0;
            params.y = MyUtils.dp2px(getApplicationContext(), 120);

            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mOverlayView, params);

            Display display = mWindowManager.getDefaultDisplay();
            final Point size = new Point();
            display.getSize(size);

            overlay_floating_pi_button_pi_button = mOverlayView.findViewById(R.id.overlay_floating_pi_button_pi_button);
            overlay_floating_pi_button_circle = mOverlayView.findViewById(R.id.overlay_floating_pi_button_circle);
            overlay_floating_pi_button_circle2 = mOverlayView.findViewById(R.id.overlay_floating_pi_button_circle2);
            overlay_floating_pi_button_circle_btn_eplus = mOverlayView.findViewById(R.id.overlay_floating_pi_button_circle_btn_eplus);
            overlay_floating_pi_button_circle_btn_nfx = mOverlayView.findViewById(R.id.overlay_floating_pi_button_circle_btn_nfx);
            overlay_floating_pi_button_circle_btn_spy = mOverlayView.findViewById(R.id.overlay_floating_pi_button_circle_btn_spy);
            overlay_floating_pi_button_circle_btn_end = mOverlayView.findViewById(R.id.overlay_floating_pi_button_circle_btn_end);


            overlay_floating_pi_button_circle_btn_end.setOnClickListener(m_Click_ProudcutListener);
            overlay_floating_pi_button_circle_btn_spy.setOnClickListener(m_Click_ProudcutListener);
            overlay_floating_pi_button_circle_btn_nfx.setOnClickListener(m_Click_ProudcutListener);
            overlay_floating_pi_button_circle_btn_eplus.setOnClickListener(m_Click_ProudcutListener);


            hideBtn(false);
            //  counterFab = (CounterFab) mOverlayView.findViewById(R.id.fabHead);
            //    counterFab.setCount(1);


            over_layout_all = (RelativeLayout) mOverlayView.findViewById(R.id.over_layout_all);
            ViewTreeObserver vto = over_layout_all.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    over_layout_all.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int width = over_layout_all.getMeasuredWidth();
                    //To get the accurate middle of the screen we subtract the width of the floating widget.
                    mWidth = size.x - width;

                }
            });


            overlay_floating_pi_button_pi_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyApplication.mySendBroadcast(getApplicationContext(), Consts.EPLUS_SHOW_FUNCTION_BAR);
//                    closeAPP(Consts.PACKAGE_NAME_eplus);


                //    closeAPP(Consts.PACKAGE_NAME_eplus);

//                    if (params.width == MyUtils.dp2px(getApplicationContext(), 170)) {
//                        hideBtn(false);
//                        params.width = MyUtils.dp2px(getApplicationContext(), 60);
//                        params.height = MyUtils.dp2px(getApplicationContext(), 60);
//
//                    } else {
//                        hideBtn(true);
//                        params.width = MyUtils.dp2px(getApplicationContext(), 170);
//                        params.height = MyUtils.dp2px(getApplicationContext(), 170);
//
//                    }
//
//                    int middle = mWidth / 2;
//                    float nearestXWall = params.x >= middle ? mWidth : 0;
//                    params.x = (int) nearestXWall;
//                    mWindowManager.updateViewLayout(mOverlayView, params);
                }
            });


            overlay_floating_pi_button_pi_button.setOnTouchListener(floatingTouchListener);

        } else {

            //    counterFab.increase();

        }


        return super.onStartCommand(intent, flags, startId);


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

    private void jumpMainActivity() {
        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        setTheme(R.style.AppTheme);
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Consts.BROADCAST_MESSAGE_intentFilter);
        registerReceiver(msgReceiver, intentFilter);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOverlayView != null)
            mWindowManager.removeView(mOverlayView);

        unregisterReceiver(msgReceiver);
    }

    private void hideBtn(boolean show) {
        if (show) {
            overlay_floating_pi_button_circle.setVisibility(View.VISIBLE);
            overlay_floating_pi_button_circle2.setVisibility(View.VISIBLE);
            overlay_floating_pi_button_circle_btn_eplus.setVisibility(View.VISIBLE);
            overlay_floating_pi_button_circle_btn_nfx.setVisibility(View.VISIBLE);
            overlay_floating_pi_button_circle_btn_spy.setVisibility(View.VISIBLE);
            overlay_floating_pi_button_circle_btn_end.setVisibility(View.VISIBLE);
        } else {
            overlay_floating_pi_button_circle.setVisibility(View.GONE);
            overlay_floating_pi_button_circle2.setVisibility(View.GONE);
            overlay_floating_pi_button_circle_btn_eplus.setVisibility(View.GONE);
            overlay_floating_pi_button_circle_btn_nfx.setVisibility(View.GONE);
            overlay_floating_pi_button_circle_btn_spy.setVisibility(View.GONE);
            overlay_floating_pi_button_circle_btn_end.setVisibility(View.GONE);
        }

    }


    private View.OnClickListener m_Click_ProudcutListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.overlay_floating_pi_button_circle_btn_eplus:
                    Intent intent =

                            getPackageManager()
                                    .getLaunchIntentForPackage(
                                            "com.cctech.evil.eplus");
                    getApplicationContext().startActivity(intent);


                    break;
                case R.id.overlay_floating_pi_button_circle_btn_spy:
                    Intent intent3 =

                            getPackageManager()
                                    .getLaunchIntentForPackage(
                                            "com.spotify.music");
                    getApplicationContext().startActivity(intent3);
                    break;

                case R.id.overlay_floating_pi_button_circle_btn_nfx:
                    Intent intent2 =

                            getPackageManager()
                                    .getLaunchIntentForPackage(
                                            "com.netflix.mediaclient");
                    getApplicationContext().startActivity(intent2);
                    break;

                case R.id.overlay_floating_pi_button_circle_btn_end:

                    System.exit(1);

                    break;


            }


        }
    };

//
//    private void jumpServiceActicity(final String channelId) {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent dialogIntent = new Intent(
//                        FloatingPIButtonService.this,
//                        PiServiceActivity.class);
//                //  dialogIntent.putExtra("BidOrderDataModel", bidOrderDataModel);
//                dialogIntent.putExtra("channelId", channelId);
//                dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(dialogIntent);
//            }
//        }, 100);
//    }


    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int dataType = intent.getIntExtra(Consts.BROADCAST_MESSAGE_KEY_DATATPYE_KEY, 0);
            MyLog.d("-------onReceive:" + dataType);
            switch (dataType) {
                case Consts.SERVER_dataType_stopView:

                    stopSelf();
                    break;

            }


        }

    }


    public View.OnTouchListener floatingTouchListener = new View.OnTouchListener() {

        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //remember the initial position.
                    initialX = params.x;
                    initialY = params.y;
                    //get the touch location
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_UP:
                    float xDiff2 = event.getRawX() - initialTouchX;
                    float yDiff2 = event.getRawY() - initialTouchY;
                    if ((Math.abs(xDiff2) < 5) && (Math.abs(yDiff2) < 5)) {
                        v.performClick();
                    }


                    return true;
                case MotionEvent.ACTION_MOVE:
                    int xDiff = Math.round(event.getRawX() - initialTouchX);
                    int yDiff = Math.round(event.getRawY() - initialTouchY);
                    //Calculate the X and Y coordinates of the view.

                    params.x = initialX + xDiff;
                    params.y = initialY + yDiff;
                    if (params.y < MyUtils.dp2px(getApplicationContext(), 120)) {
                        params.y = MyUtils.dp2px(getApplicationContext(), 120);
                    }
                    //Update the layout with new X & Y coordinates
                    mWindowManager.updateViewLayout(mOverlayView, params);


                    return true;
            }
            return false;
        }
    };


}

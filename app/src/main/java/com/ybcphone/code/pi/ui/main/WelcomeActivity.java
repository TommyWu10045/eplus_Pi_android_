package com.ybcphone.code.pi.ui.main;


import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ybcphone.code.pi.utils.Consts;
import com.ybcphone.myhttplibrary.utils.MyLog;
import com.ybcphone.myhttplibrary.utils.MyUtils;
import com.ybcphone.myhttplibrary.view.BaseActivity;
import com.ybcphone.code.pi.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.List;
//import android.content.pm.IPackageDataObserver;

/**
 * 登入
 *
 * @author forever
 */
public class WelcomeActivity extends BaseActivity {

    private final static int MY_PERMISSIONS_REQUEST_CAMERA = 10;
    private final static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 11;
    private final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 12;
    private final static int MY_PERMISSIONS_REQUEST_VIBRATE = 13;
    private final static int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 14;
    private final static int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 15;
    private final static int MY_PERMISSIONS_REQUEST_ALL = 16;


    //----------WeakReference Handler
    private static class MyHandler extends Handler {
        private final WeakReference<WelcomeActivity> finalWeakObjct;

        public MyHandler(WelcomeActivity fromObject) {
            finalWeakObjct = new WeakReference<>(fromObject);
        }

        @Override
        public void handleMessage(Message msg) {
            WelcomeActivity thisObject = finalWeakObjct.get();
            if (thisObject != null) {
                //    —>   thisObject.
                switch (msg.what) {

                }
            }
        }
    }

    private final MyHandler myHandler = new MyHandler(this);
//----------WeakReference Handler--- end


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyLog.e(".============>     .onCreate   --->   " + this.getClass().getName());

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

        Button activity_welocome_netflix = (Button) findViewById(R.id.activity_welocome_netflix);
        activity_welocome_netflix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService();
                startAPP(Consts.PACKAGE_NAME_netflix);
            }
        });

        Button activity_welocome_spotify = (Button) findViewById(R.id.activity_welocome_spotify);
        activity_welocome_spotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService();
                startAPP(Consts.PACKAGE_NAME_spotify);
            }
        });
     Button activity_welocome_end = (Button) findViewById(R.id.activity_welocome_end);
        activity_welocome_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             finish();
            }
        });


        ImageView activity_welocome_go = (ImageView) findViewById(R.id.activity_welocome_go);
        activity_welocome_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    startPP();
            }
        });
//        AnimationDrawable animationDrawable = (AnimationDrawable) activity_welocome_progress.getBackground();
//        animationDrawable.start();


    }
//
//    public static boolean deleteAppData(String packageName) {
////
////
////        getPackageSizeInfo = packageManager.getClass()
////                .getMethod("getPackageSizeInfo",
////                        String.class, Class.forName("android.content.pm.IPackageStatsObserver");
////
//
//        boolean isSuccess = false;
//        Method clearMethod;
//        Object am = null;
//        IPackageDataObserver.Stub mStub = new IPackageDataObserver.Stub() {
//            public void onRemoveCompleted(String paramAnonymousString, boolean paramAnonymousBoolean) {
//            }
//        };
//        try {
//            Class<?> activityManagerNative = Class.forName("android.app.ActivityManagerNative");
//            // android.app.IActivityManager
//            am = activityManagerNative.getMethod("getDefault").invoke(activityManagerNative);
//
//            clearMethod = am.getClass().getMethod("clearApplicationUserData", String.class, boolean.class,IPackageDataObserver.class, int.class);
//            if (clearMethod != null) {
//                Log.e("ClearCacheUtils", "clearMethod 9.0 ");
//                clearMethod.setAccessible(true);
//                isSuccess = (boolean) clearMethod.invoke(am, packageName, true, mStub, 0);
//            }
//
//        } catch (Exception localException) {
//            localException.printStackTrace();
//            Log.e("ClearCacheUtils", "Exception:" + localException.getMessage());
//            Log.e("ClearCacheUtils", "clearMethod <9.0 ");
//            try {
//                clearMethod = am.getClass().getMethod("clearApplicationUserData", String.class,IPackageDataObserver.class, int.class);
//                if(clearMethod!=null) {
//                    clearMethod.setAccessible(true);
//                    isSuccess = (boolean) clearMethod.invoke(am, packageName, mStub, 0);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                shellRun("pm clear " + packageName);
//            }
//        }
//        return isSuccess;
//    }

    private static String shellRun(String command) {
        Process process = null;
        BufferedReader bufferedReader = null;
        String result = "";
        try {
            process = Runtime.getRuntime().exec(command);
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // TODO: handle exception
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    private void startService() {

        startService(new Intent(WelcomeActivity.this, FloatingPIButtonService.class));
        startService(new Intent(WelcomeActivity.this, FloatingFunctionBarService.class));
    }



    private void startAPP(String packageName) {


        try {
            Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(packageName);
            startActivity(intent);

//            Intent activityIntent = new Intent();
//            activityIntent.setAction(packageName);//自訂的 action name
//            startActivity(activityIntent);
        } catch (ActivityNotFoundException | NullPointerException e) {
            MyLog.e("start APP error:" + e.toString());
        }

//
//        Intent activityIntent = new Intent();
//        activityIntent.setComponent(new ComponentName("com.example.serverapp","com.example.serverapp.MainActivity" ));
//        startActivity(activityIntent);


//        Intent intent3 =
//                getPackageManager()
//                        .getLaunchIntentForPackage(
//                                packagename);
//        startActivity(intent3);
    }


    private void requestPerim(String perm, int requestCode) {

        ActivityCompat.requestPermissions(WelcomeActivity.this,
                new String[]{perm},
                requestCode);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ALL:
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
            case MY_PERMISSIONS_REQUEST_VIBRATE:
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    MyUtils.toastString(WelcomeActivity.this, "授權失敗,APP會有不正常情況！");

                }
                return;


        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        askForSystemOverlayPermission();

    }


    private void askForSystemOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(WelcomeActivity.this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 234);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }


}

package com.ybcphone.myhttplibrary.utils;


import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.ybcphone.myhttplibrary.http.ParamsModel;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Base64;

public class MyUtils {
    public static String double2money(double d) {
        MyLog.d("num1 "+ d);
        try {

            java.text.DecimalFormat format = new java.text.DecimalFormat("###,###,###,###,###.#");

            return format.format(d);

        } catch (Exception e) {
            MyLog.d("error "+ e.toString());
            return d+"";
        }
    }

    public static String flaot2str1(float d) {
        try {

            java.text.DecimalFormat format = new java.text.DecimalFormat("###.#");

            return format.format(d);

        } catch (Exception e) {
            MyLog.d("error "+ e.toString());
            return d+"";
        }
    }

    //整數字串加上千元符號
    public static String str2moneypp(String d) {
        MyLog.d("num "+ d);
        try {
            int i = Integer.parseInt(d);
            java.text.DecimalFormat format = new java.text.DecimalFormat("###,###,###,###,###");
            return format.format(i);
        } catch (Exception e) {
            MyLog.d("error "+ e.toString());
            return d;
        }
    }


    public static void setWindowStatusBarColor(Activity activity, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getResources().getColor(colorResId));

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String unicodeToUTF_8_fix(String src) {

        if (null == src) {
            return null;
        }

        //  String vv= src.replace("\\\"","\"");
        //  vv= vv.replace("\"{","{");
        //  vv= vv.replace("\"[","[");
        //  vv= vv.replace("}\"","}");
        //  src= vv.replace("]\"","]");


        System.out.println("src: " + src);
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < src.length();) {
            char c = src.charAt(i);
            if (i + 6 < src.length() && c == '\\' && src.charAt(i + 1) == 'u') {
                String hex = src.substring(i + 2, i + 6);
                try {
                    out.append((char) Integer.parseInt(hex, 16));
                } catch (NumberFormatException nfe) {
                    nfe.fillInStackTrace();
                }
                i = i + 6;
            } else {
                out.append(src.charAt(i));
                ++i;
            }
        }
        return out.toString();

    }


    public static boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName());
                return result == declaredField2.getInt(cls);
            } catch (Exception e) {
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
                AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsMgr == null)
                    return false;
                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context
                        .getPackageName());
                return mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
            } else {
                return Settings.canDrawOverlays(context);
            }
        }
    }

    public static boolean isMobileNetworkAvailable(Context context) {
        try {
            ConnectivityManager connMgr = null;
            if (null == connMgr) {
                connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            }
            NetworkInfo wifiInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiInfo != null && wifiInfo.getState() == State.CONNECTED) {
                return true;
            } else if (mobileInfo != null && mobileInfo.getState() == State.CONNECTED) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
    public static boolean priceCheck2(String nowp,String hightp){

        int newp = 0;
        try {
            newp = Integer.parseInt(nowp);
        } catch (Exception e) {
            newp = 0;
        }


        int highp = 0;
        try {
            highp = Integer.parseInt(hightp);
        } catch (Exception e) {
            highp = 0;
        }

        return newp > highp;

    }
    public static float str2float(String num) {

        try {
            return Float.parseFloat(num);


        } catch (Exception e) {
            return 0;
        }
    }
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }


    public static int getSystemBarHeight(Context cc) {
        int resourceId = cc.getResources().getIdentifier("status_bar_height", "dimen", "android");
// 获取状态栏高度
        return cc.getResources().getDimensionPixelSize(resourceId);
    }

    public static String getJsonObjectTest(JSONObject jsonObject, String keyStr) throws Exception {
        if (!jsonObject.isNull(keyStr))
            return jsonObject.getString(keyStr);
        return "";
    }


    public static void shareChoseAPP(Context cc, String str) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
        intent.putExtra(Intent.EXTRA_TEXT, str);
        //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        cc.startActivity(Intent.createChooser(intent, "Share"));

    }

    public static String StringFilter2(String str) {
        // String   regEx  =  "[^a-zA-Z0-9]";
        // 清除掉所有特殊字元
        //    String regEx = "[`~!@#$%^&*()+=|{}';',<>?~！@#¥%……&*（）——+|{}【】‘；︰”“’。，、？]";
        String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#¥%……&*（）——+|{}【】‘；︰”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static String getString(Context cc, int strInt) {
        return cc.getResources().getString(strInt);
    }


    public static void showNavMapFragment(Context c, String address) {
        //http://maps.google.com/maps?f=d&saddr=startLat%20startLng&daddr=endLat%20endLng&hl=en");
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?hl=zh&mrt=loc&q=" + address));

        c.startActivity(i);

    }

    public static String conbimStr(String s1, String s2) {

        return s1 + s2;
    }

    public static String conbimStr(String s1, int s2) {

        return s1 + s2;
    }

    public static String conbimStr(String s1, float s2) {

        return s1 + s2;
    }

    public static int str2int(String num) {

        try {
            return Integer.parseInt(num);


        } catch (Exception e) {
            return 0;
        }
    }

    public static double str2double(String num) {

        try {
            return Double.parseDouble(num);


        } catch (Exception e) {
            return 0;
        }
    }

    public static void jumpCallPhone(Context cc, String phone) {
        try {
            MyLog.d("jumpCallPhone picUrl: " + phone);
            Uri uri = Uri.parse("tel:" + phone);
            Intent it = new Intent(Intent.ACTION_DIAL, uri);
            cc.startActivity(it);
        } catch (Exception e) {

            toastString(cc, "網址錯誤");

        }
    }

    public static void jumpFBActivity(Context cc, String url) {

        Intent intent = null;
        try {
            // get the Twitter app if possible
           cc. getPackageManager().getPackageInfo("com.facebook.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/?hl=en"));
        }
       cc. startActivity(intent);
    }
    public static void jumpWebViewActivity(Context cc, String url) {
        if (null == url || url.length() < 10) {
            return;
        }
        try {
            MyLog.d("jumpWebViewActivity picUrl: " + url);
            Uri uri = Uri.parse(url);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            cc.startActivity(it);
        } catch (Exception e) {

            toastString(cc, "網址錯誤");

        }
    }

    public static String myGetFileMianName(String fName) {

        try {
            File tempFile = new File(fName.trim());
            String ff = tempFile.getName();
            //   ff = ff.substring(0, ff.lastIndexOf("."));

            return ff;
        } catch (Exception e) {
            return "";
        }
    }



    public static int dp2px(Context cc,int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                cc.getResources().getDisplayMetrics());
    }


    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        }
        return sdDir.toString();

    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static String getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小", "获取失败!");
        }
        return FormetFileSize(blockSize);
    }


    private static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }


    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }


    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }


    public static boolean isFileExist(String fileName) {
        try {
            File file = new File(fileName);
            return file.exists();
        } catch (Exception e) {
            return false;
        }
    }

    public static void toDeleteFile(String fileName) {
        MyLog.e("-< toDeleteFile >-fileName: " + fileName);
        try {
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {

        }
    }


    public static void saveBitmap2JPG(Bitmap bitmap, String Fname, int quality) {
        if (isFileExist(Fname)) {
            toDeleteFile(Fname);
        }

        File file = new File(Fname);
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String getRealPathFromUR22222I(Context context, Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            ;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public static String getLocaLPath(Context context, String folder) {
        if (context == null) return null;

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            //File file = context.getExternalFilesDir("MyAccount");

            File path = context.getExternalFilesDir(null);

            if (path != null) {
                File file = new File(path, folder);

                if (file != null) {
                    if (!file.exists()) {
                        file.mkdirs();
                    }


                    return file.getAbsolutePath() + "/";
                }
            }

        }

        File f = context.getFilesDir();

        if (f != null) {
            return f.getAbsolutePath() + "/" + folder + "/";
        }

        return null;
    }


    public static String getSysPhoneInfo_sdk() {

        String phoneInfo = android.os.Build.VERSION.RELEASE;


        return phoneInfo.substring(0, 1);

    }


    /**
     * 經緯度計算兩地距離 經度（長的）longitude 緯度 latitude
     *
     * @param lon1 經度起點
     * @param lat1 緯度起點
     * @param lon2 經度終點
     * @param lat2 緯度終點
     * @return 兩地距離
     */
    public static double getDistatce_str(double lon1, double lat1, double lon2, double lat2) {
        double distance = -1.0f;
        try {


            double R = 6378137;
            double c = Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1)
                    * Math.cos(lat2) * Math.cos(lon1 - lon2);
            distance = R * Math.acos(c) * (3.14159265 / 180) / 1000;
        } catch (Exception e) {
            MyLog.e("---計算兩地距離出錯：" + e.toString());
        }


        return distance;
    }


    public static void renameFile(String file, String toFile) {
        File toBeRenamed = new File(file);
        // 检查要重命名的文件是否存在，是否是文件
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
            // myLog("e", "renameFile >>>  File does not exist: " + file);
            return;
        }

        if (isFileExist(toFile))
            toDeleteFile(toFile);

        File newFile = new File(toFile);

        // 修改文件名
        if (toBeRenamed.renameTo(newFile)) {
            MyLog.e("renameFile >>> " + toFile);
        } else {
            MyLog.e("renameFile >>>" + file + "-----Error renmaing file");
        }

    }

    public static byte[] readBytes(Uri uri, Context context) {
        // this dynamically extends to take the bytes you read
        InputStream inputStream = null;
        ByteArrayOutputStream byteBuffer = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            byteBuffer = new ByteArrayOutputStream();

            // this is storage overwritten on each iteration with bytes
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            // we need to know how may bytes were read to write them to the byteBuffer
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

            // and then we can return your byte array.
            return byteBuffer.toByteArray();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (byteBuffer != null) {
                try {
                    byteBuffer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return null;
    }


    public static Bitmap rotationBitmap(Bitmap source, int orientation) {
        Matrix matrix = new Matrix();
        if (orientation == 6) {
            matrix.postRotate(90);
        } else if (orientation == 3) {
            matrix.postRotate(180);
        } else if (orientation == 8) {
            matrix.postRotate(270);
        }
        Log.v("Chk", "Exif: " + orientation);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    /**
     * 存Bitmap到檔案
     *
     * @param bitmap 要存的Bitmap
     * @param path   本機端位置
     */
    public static void saveBitmapToFile(Bitmap bitmap, String path) {

        try {
            File file = new File(path);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }



    public static String getVerSion(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            return pm.getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    public static String getAndroidID(Context context) {
        try {
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            return "0";
        }
    }


    public static void toastString(Context context, String mess) {
        Toast.makeText(context, mess, Toast.LENGTH_SHORT).show();
    }

    public static void toastString(Context context, int messID) {
        Toast.makeText(context, context.getResources().getString(messID), Toast.LENGTH_SHORT).show();
    }



    public static Object getObjecttransfer(String personBase64) {
        if(null== personBase64 ||personBase64.length()==0){
            return null;
        }
        try {
            byte[] base64Bytes = Base64.decodeBase64(personBase64.getBytes());
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();

        } catch (Exception e) {
            e.printStackTrace();


        }
        return null;
    }

    public static String transferObject(Object object) {
        if(null == object){
            return "";
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            String personBase64 = new String(Base64.encodeBase64(baos.toByteArray()));
            return personBase64;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static void saveObject(Context cc, String idKey, Object object) {
        SharedPreferences mSharedPreferences = cc.getSharedPreferences("base64", Context.MODE_PRIVATE);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);

            String personBase64 = new String(Base64.encodeBase64(baos.toByteArray()));
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(idKey, personBase64);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //20150411
    public static String getMyUUID(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, tmPhone, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        //    myLog("e","uuid="+uniqueId);
        return uniqueId;
    }





    public static void saveSharedPreferencesData(Context cc, String key, String selfData) {
        SharedPreferences sharedPreferences = cc.getSharedPreferences("Setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, selfData);

        editor.commit();


    }

    public static String loadSharedPreferencesData(Context cc, String key) {


        try {
            SharedPreferences sharedPreferences = cc.getSharedPreferences("Setting", Context.MODE_PRIVATE);


            return sharedPreferences.getString(key, "");
        } catch (Exception e) {
            return "";
        }

    }



    public static String getParamsByKey(List<ParamsModel> params, String key) {
        MyLog.d("----tttttttt--");
        for (ParamsModel p : params) {

            MyLog.d("----tttttttt--" + p.name + "   ,   " + p.value);
            if (p.name == key) {

                return p.value;
            }

        }
        return "";

    }


    //File file = new File(dirName);
    public static void myDeleteAllDir(File file) {
        try {
            if (file.isFile() || file.listFiles().length == 0) {
                file.delete();
            } else {
                File[] files = file.listFiles();
                for (File f : files) {
                    myDeleteAllDir(f);
                    f.delete();
                }
            }
        } catch (Exception e) {
            MyLog.e("----- delete file or driection error! maybe this path is emtpy!");
        }
    }


    public static Bitmap myZoomImage(Context c, Uri uri, int fixWidth) {
        // 获取这个图片的宽和高
        Bitmap bgimage;
        try {
            bgimage = BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        int width = bgimage.getWidth();
        int height = bgimage.getHeight();

        float ra = (float) height / (float) width;
        int fixHeight = (int) ((float) fixWidth * ra);
        Log.d("nicefood", "--width: " + width + ",,,height,==> " + height);
        Log.d("nicefood", "--fixWidth: " + fixWidth + ",,,fixHeight,==> " + fixHeight);

        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算缩放率，新尺寸除原始尺寸
        float scaleWidth = ((float) fixWidth) / (float) width;
        float scaleHeight = ((float) fixHeight) / (float) height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Log.d("nicefood", "--scaleWidth: " + scaleWidth + ",,,scaleHeight,==> " + scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height, matrix, true);

        bgimage.recycle();
        return bitmap;

    }


    public static void onBack() {
        new Thread() {
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                } catch (Exception e) {

                    MyLog.e(" onBack() ERROR  " + e.toString());

                }
            }
        }.start();
    }


    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        // 获取视频的缩略图
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        //extractThumbnail 方法二次处理,以指定的大小提取居中的图片,获取最终我们想要的图片
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }


    public static Uri getImageContentUri(Context context, String filePath) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (isFileExist(filePath)) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


    /**
     * Gets the corresponding path to a file from the given content:// URI
     *
     * @param selectedVideoUri The content:// URI to find the file path from
     * @param contentResolver  The content resolver to use to perform the query.
     * @return the file path as a string
     */
    public static String getFilePathFromContentUri(Uri selectedVideoUri,
                                                   ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
//      也可用下面的方法拿到cursor
//      Cursor cursor = this.context.managedQuery(selectedVideoUri, filePathColumn, null, null, null);

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }


}

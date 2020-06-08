package com.ybcphone.myhttplibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;



/**
 * 處理Bitmap(壓縮)
 *
 * @author forever
 */
public class MyBitmap {
    public static final int TYPE_DECODE_INPUTSREAM = 0;
    public static final int TYPE_DECODE_ASSET = 1;
    public static final int TYPE_DECODE_RESOURCE = 2;
    public static final int TYPE_DECODE_FILE = 3;
    public static final int TYPE_DECODE_BYTE = 4;
    public static final int ORIGINAL_LENGTH = -1;


    public static int getMiniSize(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        return Math.min(options.outHeight, options.outWidth);
    }

    public static boolean isSquare(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        return options.outHeight == options.outWidth;
    }


    //从文件中读取Bitmap
    public static Bitmap decodeBitmapWithOrientation(String pathName, int width, int height) {
        return decodeBitmapWithSize(pathName, width, height, false);
    }

    public static Bitmap decodeBitmapWithOrientationMax(String pathName, int width, int height) {
        return decodeBitmapWithSize(pathName, width, height, true);
    }

    private static Bitmap decodeBitmapWithSize(String pathName, int width, int height,
                                               boolean useBigger) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inInputShareable = true;
        options.inPurgeable = true;
        BitmapFactory.decodeFile(pathName, options);

        int decodeWidth = width, decodeHeight = height;
        final int degrees = getImageDegrees(pathName);
        if (degrees == 90 || degrees == 270) {
            decodeWidth = height;
            decodeHeight = width;
        }

        if (useBigger) {
            options.inSampleSize = (int) Math.min(((float) options.outWidth / decodeWidth),
                    ((float) options.outHeight / decodeHeight));
        } else {
            options.inSampleSize = (int) Math.max(((float) options.outWidth / decodeWidth),
                    ((float) options.outHeight / decodeHeight));
        }

        options.inJustDecodeBounds = false;
        Bitmap sourceBm = BitmapFactory.decodeFile(pathName, options);
        return imageWithFixedRotation(sourceBm, degrees);
    }

    public static int getImageDegrees(String pathName) {
        int degrees = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(pathName);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degrees = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degrees = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degrees = 270;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return degrees;
    }

    public static Bitmap imageWithFixedRotation(Bitmap bm, int degrees) {
        if (bm == null || bm.isRecycled())
            return null;

        if (degrees == 0)
            return bm;

        final Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap result = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        if (result != bm)
            bm.recycle();
        return result;

    }



    public static Bitmap byteToBitmap(byte[] imgByte) {
        InputStream input = null;
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        input = new ByteArrayInputStream(imgByte);
        SoftReference softRef = new SoftReference(BitmapFactory.decodeStream(
                input, null, options));
        bitmap = (Bitmap) softRef.get();
        if (imgByte != null) {
            imgByte = null;
        }

        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }


    //异步加载图片
    public static interface LoadImageCallback {
        public void callback(Bitmap result);
    }

    public static void asyncLoadImage(Context context, Uri imageUri, LoadImageCallback callback) {
        new LoadImageUriTask(context, imageUri, callback).execute();
    }

    private static class LoadImageUriTask extends AsyncTask<Void, Void, Bitmap> {
        private final Uri imageUri;
        private final Context context;
        private LoadImageCallback callback;

        public LoadImageUriTask(Context context, Uri imageUri, LoadImageCallback callback) {
            this.imageUri = imageUri;
            this.context = context;
            this.callback = callback;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                InputStream inputStream;
                if (imageUri.getScheme().startsWith("http")
                        || imageUri.getScheme().startsWith("https")) {
                    inputStream = new URL(imageUri.toString()).openStream();
                } else {
                    inputStream = context.getContentResolver().openInputStream(imageUri);
                }
                return BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            callback.callback(result);
        }
    }

    public static synchronized Bitmap getBitmapwh(int type, Object fileObject, int showWidth, int showHeight, Context context, boolean inMutable) {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        int scale = 1;


        if (showWidth != -1 && showHeight != -1) {
            opts.inJustDecodeBounds = true; //true 不產生bitmap , false 產生bitmap
            createBitmapFactory(type, opts, fileObject, context);

            float scaleRation = 1f;
            int width = opts.outWidth;
            int height = opts.outHeight;

            MyLog.d("Url : {width: " + width + ",height: " + height + "}");


            if (showWidth > showHeight) {
                scaleRation = (float) width / (float) showWidth;
            } else {
                scaleRation = (float) height / (float) showHeight;
            }

            if (scaleRation != 0) {
                scale = (int) Math.ceil(scaleRation);
            } else {
                scale = 1;
            }
        }

        opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = scale;
        opts.inMutable = inMutable;

        Bitmap creatBitmap = createBitmapFactory(type, opts, fileObject, context);


        //	Bitmap roundBitmap =  getCroppedBitmap(creatBitmap, creatBitmap.getWidth());

        return creatBitmap;

    }


    public static synchronized Bitmap getBitmap(int type, Object fileObject,
                                                int showWidth,
                                                int showHeight,
                                                Context context,
                                                boolean inMutable) {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        int scale = 1;


        if (showWidth != -1 && showHeight != -1) {
            opts.inJustDecodeBounds = true; //true 不產生bitmap , false 產生bitmap
            createBitmapFactory(type, opts, fileObject, context);

            float scaleRation = 1f;
            int width = opts.outWidth;
            int height = opts.outHeight;
            if (showWidth > showHeight) {
                scaleRation = (float) width / (float) showWidth;
            } else {
                scaleRation = (float) height / (float) showHeight;
            }

            if (scaleRation != 0) {
                scale = (int) Math.ceil(scaleRation);
            } else {
                scale = 1;
            }
        }

        opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = scale;
        opts.inMutable = inMutable;

        Bitmap creatBitmap = createBitmapFactory(type, opts, fileObject, context);


        //	Bitmap roundBitmap =  getCroppedBitmap(creatBitmap, creatBitmap.getWidth());

        return creatBitmap;

    }

    public static void cutImage(String cutImageFile, int cut_x, int cut_y, String tempUploadFile) {

        Bitmap bmp = getBitmap(cutImageFile, cut_x, cut_y);
        MyUtils.saveBitmap2JPG(bmp, tempUploadFile, 100);
    }


    public static synchronized Bitmap getBitmap(
            String fileObject,
            int showWidth,
            int showHeight
    ) {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        int scale = 1;


        if (showWidth != -1 && showHeight != -1) {
            opts.inJustDecodeBounds = true; //true 不產生bitmap , false 產生bitmap
            BitmapFactory.decodeFile(fileObject, opts);
            float scaleRation = 1f;
            int width = opts.outWidth;
            int height = opts.outHeight;
            if (showWidth > showHeight) {
                scaleRation = (float) width / (float) showWidth;
            } else {
                scaleRation = (float) height / (float) showHeight;
            }

            if (scaleRation != 0) {
                scale = (int) Math.ceil(scaleRation);
            } else {
                scale = 1;
            }
        }

        opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = scale;
        opts.inMutable = false;

        Bitmap creatBitmap = BitmapFactory.decodeFile(fileObject, opts);

        return creatBitmap;

    }

    //只算width
    public static synchronized Bitmap getBitmapFromMaxWidth(int type,
                                                            Object fileObject,
                                                            int showMaxWidth,
                                                            Context context,
                                                            boolean inMutable) {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        int scale = 1;

        if (showMaxWidth >= 0) {
            opts.inJustDecodeBounds = true; //true 不產生bitmap , false 產生bitmap
            createBitmapFactory(type, opts, fileObject, context);

            float scaleRation = 1f;
            int width = opts.outWidth;
            // int height = opts.outHeight;

            if (width > showMaxWidth) {
                scaleRation = (float) width / (float) showMaxWidth;
            } else {
                scaleRation = 1;
            }

            if (scaleRation != 0) {
                scale = (int) Math.ceil(scaleRation);
            } else {
                scale = 1;
            }
        }

        opts = new BitmapFactory.Options();
//        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = scale;

        if (inMutable == true) {
            opts.inMutable = true;
        }

        Bitmap creatBitmap = createBitmapFactory(type, opts, fileObject, context);

        return creatBitmap;

    }




    //只算width
//	public static synchronized Bitmap getBitmapFromMinWidthHeight(int type,
//			Object fileObject, int showMinWidth, int showMinHeight) {
//
//		BitmapFactory.Options opts = new BitmapFactory.Options();
//		int scale = 1;
//		
//
//		return null;
//
//	}

    public static Bitmap getEmptyBitmap(int width, int height, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        return bitmap;
    }


    private static Bitmap createBitmapFactory(int type, BitmapFactory.Options opts, Object fileObject, Context context) {


        if (type == TYPE_DECODE_INPUTSREAM) {

            return BitmapFactory.decodeStream((InputStream) fileObject, null, opts);
        } else if (type == TYPE_DECODE_ASSET || type == TYPE_DECODE_FILE) {

            return BitmapFactory.decodeFile((String) fileObject, opts);
        } else if (type == TYPE_DECODE_RESOURCE) {
            InputStream is = context.getResources().openRawResource((Integer) fileObject);
            return BitmapFactory.decodeStream(is, null, opts);
        } else if (type == TYPE_DECODE_BYTE) {

            byte[] data = (byte[]) fileObject;
            return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        } else {
            return null;
        }
    }

//	public static Bitmap getOrientationBitmap(Context context, Uri uri, int width, int height) {
//		
//		Bitmap bitmap = null;
//		String filePath = getRealPathFromURI(context,uri);
//		if (!TextUtils.isEmpty(filePath)) {
//			
//			bitmap = MyBitmap.getBitmap(MyBitmap.TYPE_DECODE_FILE, filePath, width, height, context, false);
//			int orientation = getOrientation(context,uri);
//			
//			
//			if (orientation > 0) {
//				Matrix matrix = new Matrix();
//		        matrix.postRotate(orientation);
//		        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//		        
//			}
//		}
//		return bitmap;
//	}


    public static Bitmap adjustPhotoRotation2(Bitmap bm, final float orientationDegree) {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, bm.getWidth() / 2, bm.getHeight() / 2);

        // MyLog.v(" 2233333 size     ====>   x: " + bm.getWidth() / 2 + " y: " + bm.getHeight() / 2);

        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            return bm1;
        } catch (OutOfMemoryError ex) {
        }
        return null;
    }


    public static Bitmap adjustPhotoRotation(Bitmap bitmap, final float orientationDegree) {
//创建一个与bitmap一样大小的bitmap2
        Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(bitmap2);
//主要以这个对象调用旋转方法
        Matrix matrix = new Matrix();
//以图片中心作为旋转中心，旋转180°
        matrix.setRotate(orientationDegree, bitmap2.getWidth() / 2, bitmap2.getHeight() / 2);
        Paint paint = new Paint();
//设置抗锯齿,防止过多的失真
        paint.setAntiAlias(true);
        canvas.drawBitmap(bitmap, matrix, paint);

        return bitmap2;
    }




    public static double getUriBitmapSize(Context context, Uri uri) {
        byte[] bitmapData = MyUtils.readBytes(uri, context);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length, opts);


        return (double) opts.outWidth / (double) opts.outHeight;

    }


    //以最小寬高計算
    public static Bitmap getOrientationBitmapForSetingMin(Context context, Uri uri, int maxWidth, int minWidth, int minHeight) {


        byte[] bitmapData = MyUtils.readBytes(uri, context);

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;

        //是否放大
        boolean isAddScale = false;

        Bitmap originBitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length, opts);


        int originWidth = opts.outWidth;
        int originHeight = opts.outHeight;


        float scaleTotal = 1.0f;

        if (originWidth < minWidth || originHeight < minHeight) {
            isAddScale = true;

            float scaleX = ((float) minWidth / (float) originWidth);
            float scaleY = ((float) minHeight / (float) originHeight);

            if (scaleX > scaleY) {
                scaleTotal = scaleX;
            } else {
                scaleTotal = scaleY;
            }
        }


        if (isAddScale == false) {
            if (originWidth > 1000) {
                scaleTotal = 1000.0f / originWidth;
            }
        }


        opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inJustDecodeBounds = false;

        originBitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length, opts);


        Matrix matrix = new Matrix();
        matrix.postScale(scaleTotal, scaleTotal);

        int orientation = getOrientation(context, uri);
        //matrix.postRotate(orientation);

        if (orientation > 0) {
            matrix.postRotate(orientation);
        }

        originBitmap = Bitmap.createBitmap(originBitmap, 0, 0, originWidth, originHeight,
                matrix, true);

        return originBitmap;
    }

    //只對寬計算
    public static Bitmap getOrientationBitmapForSetting(Context context, Uri uri, int maxWidth) {
        Bitmap bitmap = null;
        bitmap = MyBitmap.getBitmapFromMaxWidth(MyBitmap.TYPE_DECODE_BYTE, MyUtils.readBytes(uri, context), maxWidth, context, false);

        int orientation = getOrientation(context, uri);

        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }

    public static Bitmap getOrientationBitmapForSetting(Context context, Uri uri, int width, int height) {
        Bitmap bitmap = null;
        bitmap = MyBitmap.getBitmap(MyBitmap.TYPE_DECODE_BYTE, MyUtils.readBytes(uri, context), width, height, context, false);

        int orientation = getOrientation(context, uri);

        if (orientation > 0) {
//			Matrix matrix = new Matrix();
//	        matrix.postRotate(orientation);
//	        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap = MyUtils.rotationBitmap(bitmap, orientation);
        }
        return bitmap;
    }

    public static String radomImageFile(String prefix, String suffix) {
        int randomInt = (int) (Math.random() + 1);

        return prefix + randomInt + suffix;

    }


    // get orientation.
    public static int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        try {
            ExifInterface exifInterface = new ExifInterface(photoUri.getPath());
            return exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }

    }

    public static BitmapFactory.Options getBitmapOpts(int type, Object fileObject, Context context) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        createBitmapFactory(type, opts, fileObject, context);
        return opts;
    }

    public static Bitmap myZoomImage(String uri, int newWidth, int newHeight) {

        Bitmap bgimage = getbitmapFromFileinSampleSize(uri, 1);


        int width = bgimage.getWidth();
        int height = bgimage.getHeight();
        float scalexy = 0;
        float rax = 0;
        float ray = 0;

        rax = (float) width / (float) newWidth;
        ray = (float) height / (float) newHeight;
        rax = 1 / rax;
        ray = 1 / ray;
        if (rax > ray) {
            scalexy = rax;
        } else {
            scalexy = ray;
        }


        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        matrix.postScale(scalexy, scalexy);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height, matrix, true);

        bgimage.recycle();
        return bitmap;

    }


    public static void convertViewToBitmapJPG(View view, String filePath) {
        MyLog.d("---轉換View 到本地檔案：" + filePath);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        int ww = view.getMeasuredWidth();
        int hh = view.getMeasuredHeight();

        MyLog.d("---View  Width：" + ww + ",Height： " + hh);
        view.layout(0, 0, ww, hh);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        saveBitmap2JPG(bitmap, filePath, 100);
        bitmap.recycle();

    }

    public static String setMaskPhotoFile(String maskFile, String picFile, int newWidth, int newHeight, int SampleSize, int name2) {
        // MyLog.e("----newWidth : " + newWidth);
        //  MyLog.e("-newHeight : " + newHeight);
        MyLog.e("-maskFile : " + maskFile);
        MyLog.e("-picFile : " + picFile);
        MyLog.e("-SampleSize : " + SampleSize);


        String newFile = getMaskPhotoFile(picFile) + name2;
        //newFile = newFile.replaceAll("-", "_");
        try {
            //获取图片的资源文件
            //  MyLog.e("-11111111 : ");
            //    Bitmap original  =   getFixSmallBitmap(picFile,newWidth,newHeight);
            Bitmap original = myTouchZoomImage(picFile, newWidth, newHeight, SampleSize);


            //获取遮罩层图片
            Bitmap mask = myTouchZoomImage(maskFile, newWidth, newHeight, SampleSize);

            Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
            //将遮罩层的图片放到画布中
            Canvas mCanvas = new Canvas(result);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            float offsetX = (float) (original.getWidth() - mask.getWidth()) * -1 / 2;
            float offsetY = (float) (original.getHeight() - mask.getHeight()) * -1 / 2;
            mCanvas.drawBitmap(original, offsetX, offsetY, null);
            mCanvas.drawBitmap(mask, 0, 0, paint);
            paint.setXfermode(null);
            saveBitmapToFile(result, newFile);
            mask.recycle();
            result.recycle();
            return newFile;
        } catch (Exception e) {
            MyLog.e("--- MyBitmap.java - setMaskPhotoFile  -- ERROR : " + e.toString());
            return "";

        }
    }

    public static String getMaskPhotoFile(String picFile) {
        return picFile + "mm";
    }


    /**
     * 存Bitmap到檔案
     *
     * @param bitmap 要存的Bitmap
     * @param path   本機端位置
     */
    public static String saveBitmapToFile(Bitmap bitmap, String path) {

        try {
            File file = new File(path);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return path;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }


    }


    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        ;
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    //EditPhotoTemplatesActivity.java專用
    public static float getBitmapWHRate(String uri) {
        try {
            //   MyLog.d("----------getBitmapWHRate---- ------------");
            Bitmap bgimage = getbitmapFromFileinSampleSize(uri, 2);
            float ra = (float) bgimage.getHeight() / (float) bgimage.getWidth();
            MyLog.d("bmp: {picUrl: " + uri);
            MyLog.d("圖: {getWidth: " + bgimage.getWidth() + ",getHeight: " + bgimage.getHeight() + "} +  ra:" + ra);
            bgimage.recycle();

            return ra;
        } catch (Exception e) {
            MyLog.e("----getBitmapWHRate-- ERROR : " + e.toString());
            return 1;
        }
    }


    //EditPhotoTemplatesActivity.java專用
    public static Bitmap mTemplatesImageScale(String uri, float scalexy) {
        //   MyLog.e("----------mTemplatesImageScale---- ------------");
        Bitmap bgimage = getbitmapFromFileinSampleSize(uri, 2);
        //  MyLog.d("bmp: {picUrl: " + uri);
        //  MyLog.d("目標大小: {rat: " + scalexy + "}");
        //  MyLog.d("原圖大小: {getWidth: " + bgimage.getWidth() + ",getHeight: " + bgimage.getHeight() + "}");


        int width = bgimage.getWidth();
        int height = bgimage.getHeight();


        try {


            //    MyLog.d("------ scalexy : " + scalexy);
            // 创建操作图片用的matrix对象
            Matrix matrix = new Matrix();
            matrix.postScale(scalexy, scalexy);
            Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height, matrix, true);
            //    MyLog.d("new_bmp: {getWidth: " + bitmap.getWidth() + ",getHeight: " + bitmap.getHeight() + "}");

            if (bgimage != null)
                bgimage.recycle();

            return bitmap;
        } catch (Exception e) {
            MyLog.e("----mTemplatesImageScale-- ERROR : " + e.toString());
            return null;
        }

    }

    //EditPhotoTemplatesActivity.java專用
    public static Bitmap mTemplatesImage(String uri, int newWidth, int newHeight, int fixSize) {
        //   MyLog.e("----------myTouchZoomImage---- ------------");
        Bitmap bgimage = getbitmapFromFileinSampleSize(uri, fixSize);
        if (bgimage == null) {
            return null;
        }
        //    MyLog.d("bmp: {picUrl: " + uri);
        // //   MyLog.d("目標大小: {Width: " + newWidth + ",Height: " + newHeight + "}");
        //   MyLog.d("原圖大小: {getWidth: " + bgimage.getWidth() + ",getHeight: " + bgimage.getHeight() + "}");


        int width = bgimage.getWidth();
        int height = bgimage.getHeight();
        float scalexy = 0;
        float rax = 0;
        float ray = 0;
        try {
            if (width < newWidth || height < newHeight) {//將圖放大
                //    MyLog.d("------ 將圖放大 : ");
                rax = (float) newWidth / (float) width;
                ray = (float) newHeight / (float) height;

                if (rax > ray) {
                    scalexy = rax;
                } else {
                    scalexy = ray;
                }
            } else {
                //    MyLog.d("------ 將圖 變小 : ");
                rax = (float) width / (float) newWidth;
                ray = (float) height / (float) newHeight;
                rax = 1 / rax;
                ray = 1 / ray;
                if (rax > ray) {
                    scalexy = rax;
                } else {
                    scalexy = ray;
                }
            }


            //  MyLog.d("------ scalexy : " + scalexy);
            // 创建操作图片用的matrix对象
            Matrix matrix = new Matrix();
            matrix.postScale(scalexy, scalexy);
            Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height, matrix, true);
            //   MyLog.d("new_bmp: {getWidth: " + bitmap.getWidth() + ",getHeight: " + bitmap.getHeight() + "}");
            if (bgimage != null)
                bgimage.recycle();

            return bitmap;
        } catch (Exception e) {
            MyLog.e("----mTemplatesImage-- ERROR : " + e.toString());
            return null;
        }

    }


    //ToochZoomView.java專用
    public static Bitmap myTouchZoomImage(String uri, int newWidth, int newHeight, int SampleSize) {
        Bitmap bgimage = null;
        MyLog.e("---myTouchZoomImage--1111111---: " + uri);

        //   bgimage =   getFixSmallBitmap(uri,newWidth,newHeight);
        bgimage = getbitmapFromFileinSampleSize(uri, SampleSize);

        if (bgimage == null) {
            MyLog.e("-------無法載入圖片---: " + uri);
            return null;

        }


        // MyLog.d("bmp: {picUrl: " + uri);
        //   MyLog.d("目標大小: {Width: " + newWidth + ",Height: " + newHeight + "}");
        //   MyLog.d("原圖大小: {getWidth: " + bgimage.getWidth() + ",getHeight: " + bgimage.getHeight() + "}");
        // MyLog.e("----test345----222222---: " + uri);

        int width = bgimage.getWidth();
        int height = bgimage.getHeight();
        float scalexy = 0;
        float rax = 0;
        float ray = 0;
        try {
            if (width < newWidth || height < newHeight) {//將圖放大
                //     MyLog.d("------ 將圖放大 : ");
                rax = (float) newWidth / (float) width;
                ray = (float) newHeight / (float) height;

                if (rax > ray) {
                    scalexy = rax;
                } else {
                    scalexy = ray;
                }
            } else {
                //  MyLog.d("------ 將圖 變小 : ");
                rax = (float) width / (float) newWidth;
                ray = (float) height / (float) newHeight;
                rax = 1 / rax;
                ray = 1 / ray;
                if (rax > ray) {
                    scalexy = rax;
                } else {
                    scalexy = ray;
                }
            }

            //   MyLog.e("-----333333---: " + uri);
            //  MyLog.d("------ scalexy : " + scalexy);
            // 创建操作图片用的matrix对象
            Matrix matrix = new Matrix();
            matrix.postScale(scalexy, scalexy);
            Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height, matrix, true);
            //    MyLog.d("new_bmp: {getWidth: " + bitmap.getWidth() + ",getHeight: " + bitmap.getHeight() + "}");

            if (bgimage != null)
                bgimage.recycle();

            return bitmap;
        } catch (Exception e) {
            MyLog.e("----myTouchZoomImage-- ERROR : " + e.toString());
            return null;
        }

    }


    public static Bitmap getFixSmallBitmap2(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }


    public static Bitmap getbitmapFromFileinSampleSize(String strName, int sa) {


        //  changeSizeJpgFile(strName, 1000, strName);


        Bitmap bm = null;
        if (MyUtils.isFileExist(strName)) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = sa;
                bm = BitmapFactory.decodeFile(strName, options);


                //   MyLog.d("==  getbitmapFromFileinSampleSize   -outWidth:" + options.outWidth + "----->"
                //           + "-----" + options.inSampleSize);


            } catch (Exception e) {
                bm = null;
            }
        }
        return bm;
    }


    public static void changeSizeJpgFile(String inFile, int outWidth, String outFile2) {
        Bitmap bm = null;
        // Bitmap newbm = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bm = BitmapFactory.decodeFile(inFile, options);

            options.inSampleSize = options.outWidth / outWidth;

            MyLog.d("===changeSizeJpgFile---" + options.outWidth + "----->"
                    + outWidth + "-----" + options.inSampleSize);


            if (options.inSampleSize > 1) {


                int height = options.outHeight * outWidth / options.outWidth;
                options.outWidth = outWidth;
                options.outHeight = height;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inJustDecodeBounds = false;
                options.inInputShareable = true;
                options.inPurgeable = true;
                bm = BitmapFactory.decodeFile(inFile, options);

                saveBitmap2JPG(bm, outFile2, 100);
            } else {
                MyLog.d("===--no change");
            }

        } catch (Exception e) {
            MyLog.d("==ERROR=---" + e.toString());
        }


    }


    //取得底圖的長寛比
    public static float getBitmapWHRate(String uri, IntWHModel sour) {
        try {
            //   MyLog.e("----------getBitmapWHRate--uri-> " + uri);
            float ra = 1;
            Bitmap bgimage = MyBitmap.getbitmapFromFileinSampleSize(uri, 2);
            if (null != bgimage) {
                sour.width = bgimage.getWidth();
                sour.height = bgimage.getHeight();
                ra = (float) bgimage.getHeight() / (float) bgimage.getWidth();
                //  MyLog.d("bmp: {picUrl: " + uri);
                //  MyLog.d("圖: {getWidth: " + bgimage.getWidth() + ",getHeight: " + bgimage.getHeight() + "} +  ra:" + ra);
                bgimage.recycle();
            }
            return ra;
        } catch (Exception e) {
            MyLog.e("----getBitmapWHRate-- ERROR : " + e.toString());
            return 1;
        }
    }

    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);

        return Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
    }




    public static void saveBitmap2JPG(Bitmap bitmap, String Fname, int quality) {
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
}

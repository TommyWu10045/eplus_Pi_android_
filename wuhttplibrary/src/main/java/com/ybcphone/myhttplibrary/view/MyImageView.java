package com.ybcphone.myhttplibrary.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ybcphone.myhttplibrary.R;
import com.ybcphone.myhttplibrary.http.HttpDownloadFile;
import com.ybcphone.myhttplibrary.utils.MyBitmap;
import com.ybcphone.myhttplibrary.utils.MyLog;
import com.ybcphone.myhttplibrary.utils.MyUtils;
import com.ybcphone.myhttplibrary.utils.WebAndLocalPathRule;

import java.io.File;
import java.net.URL;

public class MyImageView extends ImageView {
    private Context context;
    private int smallSize = 5;
    public String localFileName = "";
    private HttpDownloadFile m_HttpDownloadFile = new HttpDownloadFile();


    private DisplayMetrics m_DisplayMetrics = null;
    private static Bitmap tempBitmap = null;
    private Bitmap m_Bitmap = null;
    private String m_FileName = null;

    public MyImageView(Context context) {
        super(context);
        this.context = context;
        m_DisplayMetrics = MyUtils.getDisplayMetrics(getContext());
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        m_DisplayMetrics = MyUtils.getDisplayMetrics(getContext());
    }



    public void setLocalPhoto_Glide(Context cc, String localFileName) {
        File file = new File(localFileName);
        Glide.with(cc).load(file).into(this);

    }


    public void setLocalPhoto(final String fileName, final int sm) {
        //   MyLog.e("setLocalPhoto file :" + fileName);
        localFileName = fileName;
        new Thread(new Runnable() {
            public void run() {
                int showWidth = m_DisplayMetrics.widthPixels / sm;
                int showHeight = m_DisplayMetrics.heightPixels / sm;
                final Bitmap m_Bitmap = MyBitmap.getBitmap(MyBitmap.TYPE_DECODE_FILE, fileName, showWidth, showHeight, getContext(), true);
                m_FileName = fileName;
                MyImageView.this.post(new Runnable() {  // -> 利用ui元件進行post，下面那行的run會執行在ui元件所使用的thread上(Main Thread)
                    public void run() {

                        if (m_Bitmap == null) {
                            setDefaultPic();
                        } else {
                            setImageBitmap(m_Bitmap);
                        }
                    }
                });
            }
        }).start();


    }


    public void setPicFileName(String fileName) {
        m_FileName = fileName;
    }


    public String getPicFileName() {
        return m_FileName;
    }


    public void setDefaultPic() {
        recycleBitmap();
        tempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nopic);
        setImageBitmap(m_Bitmap);
    }


    private Bitmap getBitmapDefaul() {
        if (tempBitmap == null) {
            //  tempBitmap = BitmapFactory.decodeResource(getResources(), R.);
        }
        return tempBitmap;
    }

    /**
     * 回收bitmap
     */
    public void recycleBitmap() {
        if (m_Bitmap != null) {
            m_Bitmap.recycle();
        }
        m_Bitmap = null;
    }

    public void setUrlPic2(Context cc, String downUrl, int smallSize) {
        //載入前的處理

        //縮小比例
        this.smallSize = smallSize;
        MyLog.d("--2 setUrlPic   downUrl : " + downUrl);

        String fileName = MyUtils.myGetFileMianName(downUrl);
        String localFolderPath = WebAndLocalPathRule.getLocaLPath(getContext(), WebAndLocalPathRule.PICTURE_FOLDER);
        //  MyUtils.toDeleteFile(localFolderPath + fileName);z
        if (MyUtils.isFileExist(localFolderPath + fileName)) {
            localFileName = localFolderPath + fileName;
            File file = new File(localFolderPath + fileName);
            Glide.with(cc).load(file).into(this);
        } else {
            setDefaultPic();
            MyLog.d("--先去下載- ");
            m_HttpDownloadFile.doDownload(this, downUrl, localFolderPath, fileName, DownloadFinishListener_MyImageView);
        }

    }

    public void setUrlPic(String downUrl, int smallSize) {
        //載入前的處理
        //縮小比例
        this.smallSize = smallSize;

        String fileName = MyUtils.myGetFileMianName(downUrl);
        String localFolderPath = WebAndLocalPathRule.getLocaLPath(getContext(), WebAndLocalPathRule.PICTURE_FOLDER);

        if (MyUtils.isFileExist(localFolderPath + fileName)) {
            localFileName = localFolderPath + fileName;
            //   setLocalPhoto(localFolderPath + fileName, smallSize);
            File file = new File(localFileName);
            Glide.with(context).load(file).into(this);
        } else {
            setDefaultPic();
            MyLog.d("--先去下載- ");
            m_HttpDownloadFile.doDownload(this, downUrl, localFolderPath, fileName, DownloadFinishListener_MyImageView);
        }

    }


    public void setUrlPic2(String downUrl, int smallSize) {
        //載入前的處理


        //縮小比例
        this.smallSize = smallSize;

        String fileName = MyUtils.myGetFileMianName(downUrl);
        String localFolderPath = WebAndLocalPathRule.getLocaLPath(getContext(), WebAndLocalPathRule.PICTURE_FOLDER);
        if (MyUtils.isFileExist(localFolderPath + fileName)) {

            setLocalPhoto(localFolderPath + fileName, smallSize);

        } else {
            MyLog.d("--先去下載- ");
            setDefaultPic();
            m_HttpDownloadFile.doDownload(this, downUrl, localFolderPath, fileName, DownloadFinishListener_MyImageView);
        }

    }

    private HttpDownloadFile.DownloadFinishListener DownloadFinishListener_MyImageView = new HttpDownloadFile.DownloadFinishListener() {

        @Override
        public void onFinish(View view, String filename, String localFolderPath, URL webURL) {
            //   MyImageView myImageView = (MyImageView) view;
            localFileName = localFolderPath + filename;
            MyLog.d("下載完成： " + localFileName);

            //   myImageView.setLocalPhoto(localFolderPath + filename, smallSize);
            try {
                File file = new File(localFileName);
                Glide.with(context).load(file).into(MyImageView.this);



            } catch (Exception e) {

            }
        }
    };

}

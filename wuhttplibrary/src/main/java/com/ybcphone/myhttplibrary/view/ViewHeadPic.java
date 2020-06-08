package com.ybcphone.myhttplibrary.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;


import com.ybcphone.myhttplibrary.R;
import com.ybcphone.myhttplibrary.http.HttpDownloadFile;
import com.ybcphone.myhttplibrary.utils.MyBitmap;
import com.ybcphone.myhttplibrary.utils.MyLog;
import com.ybcphone.myhttplibrary.utils.MyUtils;
import com.ybcphone.myhttplibrary.utils.VeDate;
import com.ybcphone.myhttplibrary.utils.WebAndLocalPathRule;

import java.net.URL;


public class ViewHeadPic extends FrameLayout {
    private HttpDownloadFile m_HttpDownloadFile = new HttpDownloadFile();
    public int defResPic = R.drawable.icon_profile;
    public int smallSize = 1;
    public Context context = null;
    public String fileName = "";
    public String now_localFileName = "";
    public String localFolderPath = "";
    private MultiShapeView view_head_pic_head = null;

    private DisplayMetrics m_DisplayMetrics = null;
    private static Bitmap BITMAP_DEFAULT_HEAD = null;
    public Bitmap m_Bitmap = null;


    public ViewHeadPic(Context context) {
        this(context, null);
    }

    public ViewHeadPic(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        LayoutInflater.from(context).inflate(R.layout.view_head_pic, this);
        view_head_pic_head = (MultiShapeView) findViewById(R.id.view_head_pic_head);
        m_DisplayMetrics = MyUtils.getDisplayMetrics(getContext());


    }


    public void setHeadUrl(String downUrl) {
        fileName = MyUtils.myGetFileMianName(downUrl);
        MyLog.d(" =setData= downUrl :" + downUrl);
        if (fileName.length() > 0) {
            localFolderPath = WebAndLocalPathRule.getLocaLPath(context, WebAndLocalPathRule.PICTURE_FOLDER);
            if (MyUtils.isFileExist(localFolderPath + fileName)) {
                MyLog.d(" =setData= isFileExist :" + localFolderPath + fileName);
                setPhoto(localFolderPath + fileName);
            } else {
                m_HttpDownloadFile.doDownload(view_head_pic_head, downUrl,
                        localFolderPath, fileName, DownloadFinishListener_MyImageView_head);
            }
        }


    }




    public void redirectDownloadHeadPic_facebook(String url) {

        String head_localFolderPath = WebAndLocalPathRule.getLocaLPath(context, WebAndLocalPathRule.FOLDER_UPLOAD_TEMP);
        String fbHead =  "fbhead.jpgo";

        if(MyUtils.isFileExist(head_localFolderPath+fbHead)){
            MyUtils.toDeleteFile(head_localFolderPath+fbHead);
        }

        head_localFolderPath = WebAndLocalPathRule.getLocaLPath(context, WebAndLocalPathRule.FOLDER_UPLOAD_TEMP);
        fbHead =  "fbhead.jpgo";


        m_HttpDownloadFile.doDownload(null, url,
                head_localFolderPath,
                fbHead, DownloadFinishListener_MyImageView_head);




    }


    public void setHeadUrl(String downUrl, int smallS) {
        this.smallSize = smallS;
        setDefaultPic();
        fileName = MyUtils.myGetFileMianName(downUrl);
        if (fileName.length() > 0) {
            localFolderPath = WebAndLocalPathRule.getLocaLPath(context, WebAndLocalPathRule.PICTURE_FOLDER);
            if (MyUtils.isFileExist(localFolderPath + fileName)) {
                setPhoto(localFolderPath + fileName);
            } else {
                m_HttpDownloadFile.doDownload(view_head_pic_head, downUrl,
                        localFolderPath, fileName, DownloadFinishListener_MyImageView_head);
            }
        }

    }


    private HttpDownloadFile.DownloadFinishListener DownloadFinishListener_MyImageView_head = new HttpDownloadFile.DownloadFinishListener() {

        @Override
        public void onFinish(View view, String filename, String localFolderPath, URL webURL) {
            MyLog.d(" =Download= onFinish :" + localFolderPath + filename);
            setPhoto(localFolderPath + filename);

        }
    };


    public void setPhoto(final String fileName) {

        now_localFileName = fileName;
// download是耗時的動作，在另外建立一個thread來執行，所以，下一行的run()，這在個thread.start()後，會在另一個thread(worker thread)執行

        new Thread(new Runnable() {
            public void run() {
                int showWidth = m_DisplayMetrics.widthPixels / 5;
                int showHeight = m_DisplayMetrics.heightPixels / 5;
                final Bitmap m_Bitmap = MyBitmap.getBitmap(
                        MyBitmap.TYPE_DECODE_FILE,
                        fileName,
                        showWidth,
                        showHeight,
                        getContext(),
                        true);
                ViewHeadPic.this.post(new Runnable() {
                    // -> 利用ui元件進行post，下面那行的run會執行在ui元件所使用的thread上(Main Thread)
                    public void run() {
                        if (m_Bitmap == null) {
                            setDefaultPic();
                        } else {
                            // setImageBitmap(m_Bitmap);
                            view_head_pic_head.setImageBitmap(m_Bitmap);
                        }
                    }
                });
            }
        }).start();


    }


    public void setResource(int res) {

        view_head_pic_head.setImageBitmap(BitmapFactory.decodeResource(getResources(), res));

        recycleBitmap();
    }

    public void setDefaultPic() {
        if (BITMAP_DEFAULT_HEAD == null) {
            BITMAP_DEFAULT_HEAD = BitmapFactory.decodeResource(getResources(), defResPic);
        }
        view_head_pic_head.setImageBitmap(BITMAP_DEFAULT_HEAD);

    }


    private void recycleBitmap() {
        if (m_Bitmap != null) {
            m_Bitmap.recycle();
        }
        m_Bitmap = null;
    }


}

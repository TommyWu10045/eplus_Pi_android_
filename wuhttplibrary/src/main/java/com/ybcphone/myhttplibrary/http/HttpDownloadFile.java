package com.ybcphone.myhttplibrary.http;

import android.os.AsyncTask;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;


/**
 * 下載檔案(主要圖檔)
 *
 * @author Forever
 */
public class HttpDownloadFile {

    public void doDownload(View view, String downloadPath, String localFolderPath, String fileName, DownloadFinishListener downloadFinishListener) {
        DownloadFileAsyncTask downloadFileAsyncTask = new DownloadFileAsyncTask(view, localFolderPath, fileName, downloadFinishListener);
        downloadFileAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadPath);
//		downloadFileAsyncTask.execute(downloadPath);
    }


    private class DownloadFileAsyncTask extends AsyncTask<String, Integer, String> {
        private DownloadFinishListener downloadFinishListener;
        private String localFolderPath = null;
        private String fileName = null;
        private View view = null;
        private URL webURL = null;

        public DownloadFileAsyncTask(View view, String localFolderPath, String fileName, DownloadFinishListener downloadFinishListener) {
            this.downloadFinishListener = downloadFinishListener;
            this.view = view;
            this.localFolderPath = localFolderPath;
            this.fileName = fileName;
        }

        @Override
        protected String doInBackground(String... urlPath) {
            try {
                this.webURL = new URL(urlPath[0]);
                downloadFile(webURL, localFolderPath, fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            downloadFinishListener.onFinish(view, fileName, localFolderPath, webURL);
        }

    }

    /**
     * 下載檔案method
     *
     * @param url       下載位置

     * @param fileName  檔名
     */
    private void downloadFile(URL url, String localFolderPath, String fileName) {
        try
        {
            File desFile = new File(localFolderPath+fileName);

            if(desFile.exists())
            {
                return;
            }
            else if(desFile.getParentFile().exists() == false)
            {
                desFile.getParentFile().mkdirs();

            }

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            InputStream is = conn.getInputStream();
            FileOutputStream fos = new FileOutputStream(new File(localFolderPath+fileName));
            byte[] buffer = new byte[1024];
            int bytteRead;
            while ((bytteRead = is.read(buffer))!=-1)
            {
                fos.write(buffer,0,bytteRead);
            }
            fos.close();

        } catch (ProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



    }

    public interface DownloadFinishListener {
        /**
         * 下載圖片完成
         *
         * @param view            對應的VIEW
         * @param filename        檔名
         * @param localFolderPath 本機端位置
         * @param webURL          下載網址
         */
        public void onFinish(View view, String filename, String localFolderPath, URL webURL);
    }
}

package com.ybcphone.myhttplibrary.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class WebAndLocalPathRule
{
	public static final String FOLDER_UPLOAD_TEMP = "TempUpload/";
	public static final String FOLDER_NAME_ACCOUNT = "Account/";
	public static final String PICTURE_FOLDER = "Picture/";

	
	public static String getAccountIDLocalFolderPath(Context context, String accountID)
	{
		return getLocaLPath(context, FOLDER_NAME_ACCOUNT+accountID+"/");
	}


	//返回暫存區
	public static String getTempUploadFilePath(Context context) {
		return getLocaLPath(context, FOLDER_UPLOAD_TEMP);
	}


	
	public static String getAccountIDFileName(long accountID)
	{
		return accountID+".jpg";
	}
	

	
	public static String getLocaLPath(Context context, String folder)
	{
		if (context == null) return null;
		
		String state = Environment.getExternalStorageState();
	     
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			//File file = context.getExternalFilesDir("MyAccount");
			
			File path = context.getExternalFilesDir(null);
			
			if(path != null){
				File file = new File(path, folder);
				
				if (file != null) {
					if(!file.exists()){
						file.mkdirs();
					}
					
					
					return file.getAbsolutePath()+"/";
				}
			}
			
		}
		
		File f = context.getFilesDir();
		
		if (f != null) 
		{
			return f.getAbsolutePath()+ "/" + folder+"/";
		}
		
		return null;
	}
}

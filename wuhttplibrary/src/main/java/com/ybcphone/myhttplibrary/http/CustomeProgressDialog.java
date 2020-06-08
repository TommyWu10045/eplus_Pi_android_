package com.ybcphone.myhttplibrary.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ybcphone.myhttplibrary.R;


/**
 * 顯示載入中的Dialog
 * @author forevercheng
 *
 */
public class CustomeProgressDialog extends ProgressDialog
{
	public String showText="Sync Data....";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	public CustomeProgressDialog(Context context)
	{
		super(context);
	}

	public CustomeProgressDialog(Context context, int theme)
	{
		super(context, theme);
	}

	private void init()
	{
		setCanceledOnTouchOutside(false);
		setCancelable(true);
		setMessage("");
		setProgressStyle(ProgressDialog.STYLE_SPINNER);

		//设置不可取消，点击其他区域不能取消，实际中可以抽出去封装供外包设置
		setCancelable(true);
		setCanceledOnTouchOutside(true);

		setContentView(R.layout.dialog_customer_progress);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;



		ImageView activity_welocome_logo = (ImageView) findViewById(R.id.activity_welocome_logo);
		AnimationDrawable animationDrawable = (AnimationDrawable) activity_welocome_logo.getBackground();
		animationDrawable.start();


/*

		ImageView imageView = (ImageView) findViewById(R.id.dialog_customer_progress_ant);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView
				.getBackground();
		animationDrawable.start();
		animationDrawable.setOneShot(false);*/

		getWindow().setAttributes(params);
		getWindow().setBackgroundDrawable(new ColorDrawable());

	}
}

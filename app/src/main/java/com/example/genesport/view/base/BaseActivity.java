package com.example.genesport.view.base;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

//import com.example.genesport.image.ImageLoaderConfig;
import com.example.genesport.utils.AppManager;

//抽象类，可以让其它的Activity继承它。本来想写上传图片的来不及写了
public abstract class BaseActivity extends Activity {

    public static final String TAG = BaseActivity.class.getSimpleName();
	protected InputMethodManager imm;
	protected Handler mHandler = null;
	private TelephonyManager tManager;
	
	public void DisplayToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	protected abstract void findViewById();

	protected abstract void initView();

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		AppManager.getInstance().addActivity(this);
//		if (!ImageLoader.getInstance().isInited())
//			ImageLoaderConfig.initImageLoader(this, Constants.BASE_IMAGE_CACHE);
		this.tManager = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
		this.imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
		
	}
	
	protected void onDestroy() {
		super.onDestroy();
	}

	protected void onPause() {
		super.onPause();
	}

	protected void onRestart() {
		super.onRestart();
	}

	protected void onResume() {
		super.onResume();
	}

	protected void onStart() {
		super.onStart();
	}

	protected void onStop() {
		super.onStop();
	}

	protected void openActivity(Class<?> paramClass) {
		Intent localIntent = new Intent(this, paramClass);
		startActivity(localIntent);
	}

    
	/**
	 * Find the view by id in this activity
	 * 
	 * @param viewID
	 *            the view what you want to instantiation
	 * @return the view's instantiation
	 */
	@SuppressWarnings("unchecked")
	protected <T> T $(int viewID) {
		return (T) findViewById(viewID);
	}

	/**
	 * Find the view by id in appointed view
	 * 
	 * @param viewID
	 *            the view what you want to instantiation
	 * @return the view's instantiation
	 */
	@SuppressWarnings("unchecked")
	protected <T> T $with(View view, int viewID) {
		return (T) view.findViewById(viewID);
	}
	
}
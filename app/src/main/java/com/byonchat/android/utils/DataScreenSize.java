package com.byonchat.android.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class DataScreenSize {
	private WindowManager wm;
	private Display display;
	private Point size;
	private static DataScreenSize dsc;
	private int width;
	private int height;
	
	public static DataScreenSize instance(Context context){
		if(null == dsc){
			dsc = new DataScreenSize(context);
		}
		return dsc;
	}
	
	public DataScreenSize(Context context){
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		display = wm.getDefaultDisplay();
		size = new Point();
	}
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public int getWidth(){
		if(android.os.Build.VERSION.SDK_INT >= 13){ 
			display.getSize(size); 
			width = size.x;
		}else{
			width = display.getWidth();
		}
		return width;
	}
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public int getHeight(){
		if(android.os.Build.VERSION.SDK_INT >= 13){ 
			display.getSize(size); 
			height = size.y;
		}else{
			height = display.getHeight();
		}
		return height;
	}
	
	public static int DpToPixel(float dp, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = dp * (metrics.densityDpi / 160f);
	    return (int)px;
	}
	
	public static int PixelToDP(Context context, int pixel) {
	    final float scale = context.getResources().getDisplayMetrics().density;
	    return (int) ((pixel * scale) + 0.5f);
	    //Resources resources = context.getResources();
	    //DisplayMetrics metrics = resources.getDisplayMetrics();
	    //float dp = pixel / (metrics.densityDpi / 160f);
	    //return (int)dp;
	}
}

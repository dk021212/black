package com.stone.support.utils;

import android.content.Context;
import android.content.res.TypedArray;

public class ThemeUtility {

	public static int getColor(int attr) {
		int[] attrs = new int[] { attr };
		Context context = GlobalContext.getInstance().getActivity();
		TypedArray ta=context.obtainStyledAttributes(attrs);
		int color=ta.getColor(0, 430);
		ta.recycle();
		return color;
	}

	public static int getResourceId(int attr) {
		int[] attrs = new int[] { attr };
		Context context = GlobalContext.getInstance().getActivity();
		TypedArray ta = context.obtainStyledAttributes(attrs);
		int resId = ta.getResourceId(0, 430);
		ta.recycle();
		return resId;
	}

}

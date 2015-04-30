package com.stone.support.utils;

import com.stone.black.R;

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
	
	 //android:actionModeShareDrawalbe is not a public attr
    public static int getActionBarShareItemIcon() {
        return R.drawable.ic_menu_share_holo_dark;
    }

}

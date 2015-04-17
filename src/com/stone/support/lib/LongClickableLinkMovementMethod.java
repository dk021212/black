package com.stone.support.lib;

import android.text.method.ScrollingMovementMethod;

public class LongClickableLinkMovementMethod extends ScrollingMovementMethod {
	
	private boolean longClickable = true;
	
	public void setLongClickable(boolean value) {
        this.longClickable = value;
    }
	
	public static LongClickableLinkMovementMethod getInstance() {
        if (sInstance == null)
            sInstance = new LongClickableLinkMovementMethod();

        return sInstance;
    }
	
	private static LongClickableLinkMovementMethod sInstance;

}

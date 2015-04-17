package com.stone.bean.android;

import java.io.Serializable;
import java.util.TreeSet;

public class TimeLinePosition implements Serializable {
    public TimeLinePosition(int position, int top) {
        this.position = position;
        this.top = top;
    }

    public int position = 0;
    public int top = 0;
    public TreeSet<Long> newMsgIds = null;
}
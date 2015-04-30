package com.stone.bean.android;

import android.os.Bundle;

import com.stone.support.error.WeiboException;

public class AsyncTaskLoaderResult<E> {
    public E data;
    public WeiboException exception;
    public Bundle args;
}

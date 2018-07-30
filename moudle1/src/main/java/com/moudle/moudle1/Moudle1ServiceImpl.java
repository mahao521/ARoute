package com.moudle.moudle1;

import android.nfc.Tag;
import android.util.Log;

import com.moudle.base.TestService;
import com.moudle.router_annotation.Route;

/**
 * Created by Administrator on 2018/7/27.
 */
@Route(path = "/moudle1/service")
public class Moudle1ServiceImpl implements TestService {

    private static final String TAG = "Moudle1ServiceImpl";
    @Override
    public void getTest() {
        Log.d(TAG, "getTest: 我是module 1");
    }
}

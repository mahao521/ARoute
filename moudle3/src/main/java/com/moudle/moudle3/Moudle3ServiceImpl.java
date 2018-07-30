package com.moudle.moudle3;

import android.util.Log;

import com.moudle.base.TestService;
import com.moudle.router_annotation.Route;

/**
 * Created by Administrator on 2018/7/23.
 */

@Route(path = "/moudle3/service")
public class Moudle3ServiceImpl implements TestService {

    private static final String TAG = "Moudle3ServiceImpl";
    @Override
    public void getTest() {
        Log.d(TAG, "getTest: 我是module 3");
    }
}

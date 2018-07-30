package com.moudle.moudleproduct;

import android.util.Log;

import com.moudle.base.TestService;
import com.moudle.router_annotation.Route;

/**
 * Created by Administrator on 2018/7/27.
 */
@Route(path = "/main/service")
public class MainServiceImpl implements TestService {

    @Override
    public void getTest() {
        Log.i("main","我是主模块");
    }
}

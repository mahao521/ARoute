package com.moudle.router_core;

import android.app.Application;
import android.util.Log;

import com.moudle.router_core.template.IRouteRoot;
import com.moudle.router_core.utils.ClassUtils;

import java.util.Set;

/**
 * Created by Administrator on 2018/7/26.
 */

public class DNRouter {
    private static final String TAG = "DNRouter";
    private static final String ROUTER_ROOT_PACKAGE = "com.mahao.router.routes";
    private static final String SDK_NAME = "DNRouter";
    private static final String SEPARATOR = "$$";
    private static final String SUFFIX_ROOT = "Root";
    private static Application mContext;

    private DNRouter(){
    }
    private static DNRouter instance ;
    public static DNRouter getInstance(){
        if(instance == null){
            synchronized (DNRouter.class){
                instance = new DNRouter();
            }
        }
        return instance;
    }

    public static void init(Application application){
        mContext = application;
        try {
            loadInfo();
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,"初始化失败！",e);
        }
    }

    private static void loadInfo() throws Exception{
       //获取所有apt生成的路由类的全类名（路由表）
        Set<String> routerMap = ClassUtils.getFileNameByPackageName(mContext,ROUTER_ROOT_PACKAGE);
        for(String className : routerMap){
            if(className.startsWith(ROUTER_ROOT_PACKAGE+"."+SDK_NAME+SEPARATOR+SUFFIX_ROOT)){
                //root中注册是分组信息，将分组信息加入到仓库中
                ((IRouteRoot)(Class.forName(className).getConstructor().newInstance())).loadInto(Warehouse.groupsIndex);
            }
        }
    }
}

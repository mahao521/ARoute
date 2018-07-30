package com.moudle.router_core;

import android.app.Activity;
import android.util.LruCache;

import com.moudle.router_annotation.Extra;
import com.moudle.router_core.template.IExtra;

/**
 * Created by Administrator on 2018/7/27.
 */

public class ExtraManager {

    public static final String SUFFIX_AUTOWIRED = "$$Extra";
    private static ExtraManager instance;
    private LruCache<String,IExtra> mLruCache;

    public static ExtraManager getInstance(){
        if(instance == null){
            synchronized (ExtraManager.class){
                if(instance == null){
                    instance = new ExtraManager();
                }
            }
        }
        return instance;
    }

    private ExtraManager(){
        mLruCache = new LruCache<>(90);
    }

    public void loadExtras(Activity instance){
        //查找对应的activity缓存
        String className = instance.getClass().getName();
        IExtra iExtra = mLruCache.get(className);
        try {
            if(null == iExtra){
                iExtra = (IExtra) Class.forName(instance.getClass().getName() + SUFFIX_AUTOWIRED).getConstructor().newInstance();
            }
            iExtra.loadExtra(instance);
            mLruCache.put(className,iExtra);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

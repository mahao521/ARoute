package com.moudle.router_core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.moudle.router_annotation.RouteMeta;
import com.moudle.router_core.callback.NavigationCallback;
import com.moudle.router_core.template.IRouteGroup;
import com.moudle.router_core.template.IRouteRoot;
import com.moudle.router_core.template.IService;
import com.moudle.router_core.utils.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.net.NoRouteToHostException;
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

    public Postcard build(String path){
        if(TextUtils.isEmpty(path) ){
            throw  new RuntimeException("路由地址无效");
        }else{
            return new Postcard(path,extractGroup(path));
        }
    }

    public Postcard build(String path , String group){
        if(TextUtils.isEmpty(path) || TextUtils.isEmpty(group)){
            throw  new RuntimeException("路由地址无效");
        }else{
            return new Postcard(path ,group);
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

    protected Object navigation(Context context,final Postcard postcard, final int requestedCode,
                                final NavigationCallback callback){
        try{
            prepareCard(postcard);
        }catch (Exception e){
            e.printStackTrace();
            if(callback != null){
                callback.onLost(postcard);
            }
            return null;
        }
        if(null != callback){
            callback.onLost(postcard);
        }
        switch (postcard.getType()){
            case ACTIVITY:
                final Context currentContext = null == context ?  mContext : context;
                final Intent intent = new Intent(currentContext,postcard.getDestination());
                intent.putExtras(postcard.getExtras());
                int flags = postcard.getFlags();
                if(-1 != flags){
                    intent.setFlags(flags);
                }else if(!(currentContext instanceof Activity)){
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(requestedCode > 0){
                            ActivityCompat.startActivityForResult((Activity) currentContext,intent,requestedCode,postcard.getOptionsBundle());
                        }else {
                            ActivityCompat.startActivity(currentContext,intent,postcard.getOptionsBundle());
                        }
                        if((0 != postcard.getEnterAnim() || 0 != postcard.getEnterAnim()) && currentContext instanceof Activity){
                            ((Activity)currentContext).overridePendingTransition(postcard.getEnterAnim(),postcard.getExitAnim());
                        }
                        //跳转完成
                        if(null != callback){
                            callback.onArrival(postcard);
                        }
                    }
                });
            case ISERVICE:
                return postcard.getService();
            default:
                    break;
        }
        return null;
    }

    /**
     *   准备卡片
     * @param postcard
     */
    public void prepareCard(Postcard postcard){
        RouteMeta routeMeta = Warehouse.routes.get(postcard.getPath());
        if(routeMeta == null){
            //c创建并调用loadInto函数 然后记录在仓库
            Class<? extends IRouteGroup> groupMeta = Warehouse.groupsIndex.get(postcard.getGroup());
            if (null == groupMeta){
                throw  new RuntimeException("没有找到对应的路由： " + postcard.getGroup() + " " + postcard.getPath());
            }
            IRouteGroup iRouteGroup = null;
            try{
                iRouteGroup = groupMeta.getConstructor().newInstance();
            }catch (Exception e){
                e.printStackTrace();
            }
            if(iRouteGroup != null){
                iRouteGroup.loadInto(Warehouse.routes);
            }
            //已经准备过了，就移除（d否则一直在内存中）
            Warehouse.groupsIndex.remove(postcard.getGroup());
            //再次进入else
            prepareCard(postcard);
        }else {
            //类跳转的activity 或者 Iservice实现类
            postcard.setDestination(routeMeta.getDestination());
            postcard.setType(routeMeta.getType());
            switch (routeMeta.getType()){
                case ISERVICE:
                    Class<?> destination = routeMeta.getDestination();
                    IService service = Warehouse.services.get(destination);
                    if(null == service){
                        try {
                            service = (IService) destination.getConstructor().newInstance();
                            Warehouse.services.put(destination,service);
                        }  catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    postcard.setService(service);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 获得组别
     *
     * @param path
     * @return
     */
    private String extractGroup(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new RuntimeException(path + " : 不能提取group.");
        }
        try {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            if (TextUtils.isEmpty(defaultGroup)) {
                throw new RuntimeException(path + " : 不能提取group.");
            } else {
                return defaultGroup;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void inject(Activity instance){
    }
}















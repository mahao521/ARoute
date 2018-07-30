package com.moudle.router_compiler.processor;

import com.google.auto.service.AutoService;
import com.moudle.router_annotation.Route;
import com.moudle.router_compiler.processor.utils.Constants;
import com.moudle.router_compiler.processor.utils.Log;
import com.moudle.router_compiler.processor.utils.Utils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.moudle.router_annotation.RouteMeta;

/**
 * Created by Administrator on 2018/7/24.
 */
@AutoService(Processor.class)
/**
 *  处理器接收参数，替代AbstractProcessor#getSupportedOptions()函数
 */
@SupportedOptions(Constants.ARGUMENTS_NAME)
/**
 *  制定使用的java版本替代getsupportSourceVersion()函数
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
/**
 *  注册给哪些注解，替代@getSupportAnnotationTypes()函数
 */
@SupportedAnnotationTypes({"com.moudle.router_annotation.Route"})
public class RouterProcessor  extends AbstractProcessor{

    /**
     *  key:组名 value：类名
     */
    private Map<String,String> rootMap = new TreeMap<>();
    /**
     * 分组 key：组名， value :对应组的路由信息
     */
    private Map<String,List<RouteMeta>>  groupMap = new HashMap<>();

    /**
     *  节点工具类，类，属性都是节点
     */
    private Elements mElements;
    /**
     *  文件生成器 类/资源
     */
    private Filer fileUtils;
    /**
     *  type 类信息工具类
     */
    private Types mTypeUtils;
    private String moduleName;
    private Log mLog;

    /**
     *  初始化，从processingEnvironment中或得乙烯类处理器工具
     * @param processingEnvironment
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        //获取apt的日志输出
        mLog = Log.newLog(processingEnvironment.getMessager());
        mElements = processingEnv.getElementUtils();
        mTypeUtils = processingEnvironment.getTypeUtils();
        fileUtils = processingEnv.getFiler();
        //参数是模块名 为了防止多模块/组件化开发的时候，生成相同的文件
        Map<String,String> options = processingEnv.getOptions();
        if(!Utils.isEmpty(options)){
            moduleName = options.get(Constants.ARGUMENTS_NAME);
        }
        mLog.i("RouteProcessor Paramters:" + moduleName);
        if(Utils.isEmpty(moduleName)){
            throw  new RuntimeException("not set Processor Paraters");
        }
    }

    /**
     *  相当于main函数，正式处理注解
     * @param set  使用了支持处理注解的节点的集合
     * @param roundEnvironment 当前运行环境，可以通过该对象找到注解
     * @return true 表示后续处理器不会再处理
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mLog.i("Route class: " + set.toString());
        //使用了需要处理的注解
        if(!Utils.isEmpty(set)){
            //获取所有被Rote注解的元素的集合
            mLog.i("Route class: 1" + set.toString());
            Set<? extends Element> routeElemts = roundEnvironment.getElementsAnnotatedWith(Route.class);
            //处理ROute注解
            if(!Utils.isEmpty(routeElemts)){
                mLog.i("Route class: 2 " + set.toString());
                try{
                    parseRoute(routeElemts);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    private void parseRoute(Set<? extends Element> routeElemts) throws Exception{
        //支持配置路由类的类型
        TypeElement activity = mElements.getTypeElement(Constants.ACTIVITY);
        //节点描述
        TypeMirror type_activity = activity.asType();

        TypeElement iService = mElements.getTypeElement(Constants.ISERVICE);
        TypeMirror type_Iservice = iService.asType();

        //groupMap(组名：路由信息）集合
        for(Element element : routeElemts){
            //路由信息
            RouteMeta  routeMeta;
            //使用Route注解的信息
            TypeMirror tm = element.asType();
            mLog.i("Route class: " + tm.toString());
            Route route = element.getAnnotation(Route.class);
            //是否是Activity使用了Route注解
            if(mTypeUtils.isSubtype(tm,type_activity)){
                routeMeta = new RouteMeta(RouteMeta.Type.ACTIVITY,route,element);
            }else  if(mTypeUtils.isSubtype(tm,type_Iservice)){
                routeMeta = new RouteMeta(RouteMeta.Type.ISERVICE,route,element);
            }else {
                throw new RuntimeException("[just Support Activity/IService Route] :" + element);
            }
            //分组信息记录 groupMap<Group分组，RouteMeta路由信息>集合
            categories(routeMeta);
        }
        //生成类需要实现的接口
        TypeElement isRouteGroup = mElements.getTypeElement(Constants.IROUTE_GROUP);
        TypeElement isRouteRoot = mElements.getTypeElement(Constants.IROUTE_ROOT);

        //生成Group类，作用： 记录<地址，RouteMeta路由信息，Class文件信息）
        generatedGroup(isRouteGroup);

        //生成root类，作用：记录<分组，对应的group类>
        generatedRoot(isRouteRoot,isRouteGroup);
    }

    private void generatedRoot(TypeElement isRouteRoot, TypeElement isRouteGroup) throws Exception{
        //类型Map<String,Class<? extends IRouteGroup>> routes>
        //wildcard通配符
        ParameterizedTypeName routers = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                WildcardTypeName.subtypeOf(ClassName.get(isRouteGroup)))
        );
        //参数 Map<String ,Class<? extends IRoutes>> routes
        ParameterSpec rootParanSpec = ParameterSpec.builder(routers,"routes").build();
        //函数 public void loadInfo(Map<String,Class<? extends IRouteGroup>> routes> routes)
        MethodSpec.Builder loadIntoMethodOfRootBuilder = MethodSpec.methodBuilder(Constants.METHOD_LOAD_INTO)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(rootParanSpec);
        mLog.i("generatedRoot " + loadIntoMethodOfRootBuilder);
        //函数体
        for(Map.Entry<String,String> entry : rootMap.entrySet()){
            loadIntoMethodOfRootBuilder.addStatement("routes.put($S,$T.class)",entry.getKey()
                    ,ClassName.get(Constants.PACKAGE_OF_GENERATE_FILE,entry.getValue()));
        }
        //生成$Root$类
        String className = Constants.NAME_OF_ROOT + moduleName;
        JavaFile.builder(Constants.PACKAGE_OF_GENERATE_FILE,
                TypeSpec.classBuilder(className)
                        .addSuperinterface(ClassName.get(isRouteRoot))
                        .addModifiers(Modifier.PUBLIC)
                .addMethod(loadIntoMethodOfRootBuilder.build())
                .build()
         ).build().writeTo(fileUtils);
        mLog.i("Generated RouteRoot : " + Constants.PACKAGE_OF_GENERATE_FILE + "." + className);
    }

    private void generatedGroup(TypeElement isRouteFroup) throws Exception {
        //参数 Map<String,RouteMeta>
        ParameterizedTypeName atlas = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouteMeta.class));
        //参数 Map<String RouteMeta> atlas
        ParameterSpec groupParamSpec = ParameterSpec.builder(atlas,"atlas").build();
        //遍历分组，每一个分组创建一个$$Group$$类
        for(Map.Entry<String,List<RouteMeta>> entry : groupMap.entrySet()){
            /**
             *  类成员函数 loadinfo声明构建
             */
            MethodSpec.Builder loadIntoMethodofGroupBuild = MethodSpec.methodBuilder
                    (Constants.METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(groupParamSpec);
            //分组名 与 对应分组中的信息
            String groupName = entry.getKey();
            List<RouteMeta> groupData = entry.getValue();
            //遍历分组中的条目数据
            for (RouteMeta routeMeta : groupData){
                //组装函数
                loadIntoMethodofGroupBuild.addStatement(
                        "atlas.put($S,$T.build($T.$L,$T.class,$S,$S))",
                        routeMeta.getPath(),
                        ClassName.get(RouteMeta.class),
                        ClassName.get(RouteMeta.Type.class),
                        routeMeta.getType(),
                        ClassName.get((TypeElement)routeMeta.getElement()),
                        routeMeta.getPath().toLowerCase(),
                        routeMeta.getGroup().toLowerCase());
            }
            //创建java文件（$$Group$$）组
            String groupClassName = Constants.NAME_OF_GROUP+groupName;
            JavaFile.builder(Constants.PACKAGE_OF_GENERATE_FILE,
                    TypeSpec.classBuilder(groupClassName)
            .addSuperinterface(ClassName.get(isRouteFroup))
            .addModifiers(Modifier.PUBLIC)
            .addMethod(loadIntoMethodofGroupBuild.build())
            .build()
            ).build().writeTo(fileUtils);
            mLog.i("generted RouteGroup " + Constants.PACKAGE_OF_GENERATE_FILE + "." + groupClassName);
            //分组名生成对应的Group类类名
            rootMap.put(groupName,groupClassName);
        }
    }

    private void categories(RouteMeta route) {
        if(routeVerify(route)){
            mLog.i("Group info,Group Name = " + route.getGroup() + ", path = " + route.getPath());
            List<RouteMeta> routeMetas = groupMap.get(route.getGroup());
            if(Utils.isEmpty(routeMetas)){
                List<RouteMeta> routeMetasSet = new ArrayList<>();
                routeMetasSet.add(route);
                groupMap.put(route.getGroup(),routeMetasSet);
            }else {
                routeMetas.add(route);
            }
        }else{
            mLog.i("Group Info Error:" + route.getPath());
        }
    }

    public boolean routeVerify(RouteMeta meta){
        String path = meta.getPath();
        String group = meta.getGroup();
        //路由地址必须以/开头
        if(Utils.isEmpty(path) || !path.startsWith("/")){
            return false;
        }
        //如果没有设置分组，以第一个/ 后的节点为分组（所以必须是2个path)
        if(Utils.isEmpty(group)){
            String defaultGroup = path.substring(1,path.indexOf("/",1));
            if(Utils.isEmpty(defaultGroup)){
                return false;
            }
            meta.setGroup(defaultGroup);
            return true;
        }
        return true;
    }

}

package com.moudle.router_compiler.processor.utils;

import com.squareup.javapoet.ClassName;

/**
 * Created by Administrator on 2018/7/24.
 */

public class Constants {
     public static final ClassName ROUTE = ClassName.get("","");
    public static final String ARGUMENTS_NAME = "moduleName";
    public static final String ANN_TYPE_ROUTE = "com.moudle.router_annotation.Route";
    public static final String ANN_TYPE_Extra = "com.moudle.router_annotation.Extra";

    public static final String ACTIVITY = "android.app.Activity";
    public static final String ISERVICE = "com.moudle.router_core.template.IService";

    public static final String IROUTE_GROUP = "com.moudle.router_core.template.IRouteGroup";
    public static final String IROUTE_ROOT = "com.moudle.router_core.template.IRouteRoot";

    public static final String METHOD_LOAD_INTO = "loadInto";
    public static final String METHOD_LOAD_EXTRA = "loadExtra";

    public static final String SEPARATOR = "$$";
    public static final String PROJECT = "DNRouter";
    public static final String NAME_OF_ROOT = PROJECT + SEPARATOR + "Root" + SEPARATOR;
    public static final String NAME_OF_GROUP = PROJECT + SEPARATOR + "Group" + SEPARATOR;

    public static final String PACKAGE_OF_GENERATE_FILE = "com.mahao.router.routes";

}

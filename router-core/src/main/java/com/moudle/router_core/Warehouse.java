package com.moudle.router_core;

import com.moudle.router_annotation.RouteMeta;
import com.moudle.router_core.template.IRouteGroup;
import com.moudle.router_core.template.IService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lance
 * @date 2018/2/22
 */

public class Warehouse {

    // root 映射表 保存分组信息
    static Map<String, Class<? extends IRouteGroup>> groupsIndex = new HashMap<>();

    // group 映射表 保存组中的所有数据
    static Map<String, RouteMeta> routes = new HashMap<>();

    // group 映射表 保存组中的所有数据
    static Map<Class, IService> services = new HashMap<>();


}

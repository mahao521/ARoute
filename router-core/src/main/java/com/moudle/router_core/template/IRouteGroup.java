package com.moudle.router_core.template;


import com.moudle.router_annotation.RouteMeta;

import java.util.Map;

/**
 * @author Lance
 * @date 2018/2/22
 */

public interface IRouteGroup {

    void loadInto(Map<String, RouteMeta> atlas);
}

package com.moudle.router_compiler.processor.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Created by Administrator on 2018/7/24.
 */

public class Utils {

    public static boolean isEmpty(CharSequence charSequence){
        return charSequence == null || charSequence.length() == 0;
    }

    public static boolean isEmpty(Collection<?> collections){
        return collections == null || collections.isEmpty();
    }
    public static boolean isEmpty(final Map<?,?> map){
        return map == null || map.isEmpty();
    }
}

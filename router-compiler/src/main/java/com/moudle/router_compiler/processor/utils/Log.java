package com.moudle.router_compiler.processor.utils;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * Created by Administrator on 2018/7/24.
 */

public class Log {
    private Messager mMessager;

    public Log(Messager messager) {
        mMessager = messager;
    }

    public static Log newLog(Messager messager){
        return new Log(messager);
    }

    public void i(String msg){
        mMessager.printMessage(Diagnostic.Kind.NOTE,msg);
    }
}

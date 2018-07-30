/*
package com.moudle.aptprocessor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.awt.Dialog;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileManager;


@AutoService(Processor.class)
//当前注解处理器能够处理的注解 代替getsSupportAnnotionType函数
@SupportedAnnotationTypes({"com.moudle.router_annotation.Route"})
//java版本代替getSupportSourceVersion函数
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class testProcessor extends AbstractProcessor {

    private Messager mMessager;
    Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mMessager = processingEnvironment.getMessager();
        mMessager.printMessage(Diagnostic.Kind.NOTE,"testProcessor ==== init");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mMessager.printMessage(Diagnostic.Kind.NOTE,"testProcessor ===== types");
        for (TypeElement typeElement : set){
            MethodSpec main = MethodSpec.methodBuilder("main")
                    .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                    .returns(void.class)
                    .addParameter(String[].class,"args")
                    .addStatement("$T.out.println($S)",System.class,"Hello，javapoet")
                    .build();
            TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                    .addModifiers(Modifier.PUBLIC,Modifier.FINAL)
                    .addMethod(main)
                    .build();
            JavaFile javaFile = JavaFile.builder("com.moudle.moudleproduct",helloWorld)
                    .build();
            try {
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


}
*/

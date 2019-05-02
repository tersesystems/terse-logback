package com.tersesystems.logback.slf4jgen;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.slf4j.Marker;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SLF4JGenerator {

    public static void main(String[] args) throws IOException {
        Iterable<MethodSpec> traceMethods = methods("trace");
        Iterable<MethodSpec> debugMethods = methods("debug");
        Iterable<MethodSpec> infoMethods = methods("info");
        Iterable<MethodSpec> warnMethods = methods("warn");
        Iterable<MethodSpec> errorMethods = methods("error");
        TypeSpec loggerType = TypeSpec.classBuilder("PredicateLogger")
                .addModifiers(Modifier.PUBLIC)
                .addMethods(traceMethods)
                .addMethods(debugMethods)
                .addMethods(infoMethods)
                .addMethods(warnMethods)
                .addMethods(errorMethods)
                .build();

        JavaFile javaFile = JavaFile.builder("com.tersesystems.logback.ext.predicate", loggerType)
                .build();

        javaFile.writeTo(System.out);
    }

    public static Iterable<MethodSpec> methods(String methodName) {
        MethodSpec stringMethod = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(String.class, "message")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        MethodSpec stringArgMethod = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(String.class, "format")
                .addParameter(Object.class, "arg")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        //     public void trace(String format, Object arg1, Object arg2);
        MethodSpec stringArg1Arg2Method = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(String.class, "format")
                .addParameter(Object.class, "arg1")
                .addParameter(Object.class, "arg2")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        //     public void trace(String format, Object... arguments);
        MethodSpec stringArgArrayMethod = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(String.class, "format")
                .addParameter(Object[].class, "arguments")
                .varargs(true)
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        //public void trace(String msg, Throwable t);
        MethodSpec stringThrowableMethod = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(Throwable.class, "t")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        // public boolean isTraceEnabled(Marker marker);
        MethodSpec isEnabledMarkerMethod = MethodSpec.methodBuilder("is" + methodName + "Enabled")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(Marker.class, "marker")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        // public void trace(Marker marker, String msg);
        MethodSpec markerStringMethod = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(Marker.class, "marker")
                .addParameter(Object.class, "arg")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        // public void trace(Marker marker, String format, Object arg);
        MethodSpec markerFormatArgMethod = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(Marker.class, "marker")
                .addParameter(String.class, "format")
                .addParameter(Object.class, "arg")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        //     public void trace(Marker marker, String format, Object arg1, Object arg2);
        MethodSpec markerFormatArg1Arg2Method = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(Marker.class, "marker")
                .addParameter(String.class, "format")
                .addParameter(Object.class, "arg1")
                .addParameter(Object.class, "arg2")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        //     public void trace(Marker marker, String format, Object... argArray);
        MethodSpec markerFormatArgArrayMethod = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(Marker.class, "marker")
                .addParameter(String.class, "format")
                .addParameter(Object[].class, "argArray")
                .varargs(true)
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        // public void trace(Marker marker, String msg, Throwable t);
        MethodSpec markerMsgThrowableMethod = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(Marker.class, "marker")
                .addParameter(String.class, "msg")
                .addParameter(Throwable.class, "t")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        List<MethodSpec> methodList = new ArrayList<>();

        methodList.add(stringMethod);
        methodList.add(stringArgMethod);
        methodList.add(stringArg1Arg2Method);
        methodList.add(stringThrowableMethod);
        methodList.add(stringArgArrayMethod);
        methodList.add(isEnabledMarkerMethod);
        methodList.add(markerStringMethod);
        methodList.add(markerFormatArgMethod);
        methodList.add(markerFormatArg1Arg2Method);
        methodList.add(markerFormatArgArrayMethod);
        methodList.add(markerMsgThrowableMethod);
        return methodList;
    }

}

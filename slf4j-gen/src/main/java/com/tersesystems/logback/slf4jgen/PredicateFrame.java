package com.tersesystems.logback.slf4jgen;

import com.squareup.javapoet.MethodSpec;
import org.slf4j.event.Level;

public interface PredicateFrame extends LoggerFrame {

    String predicate();

    String delegate();

    default MethodSpec getNameMethodBody(MethodSpec.Builder builder) {
        return builder.addStatement("return $L.getName()", delegate()).build();
    }

    // public boolean isTraceEnabled();
    default MethodSpec isEnabledMethodBody(Level level, MethodSpec.Builder builder) {
        return builder
                .beginControlFlow("if (! $L.test($T.$L))", predicate(), Level.class, level)
                .addStatement("return false", delegate(), builder.build())
                .nextControlFlow("else")
                .addStatement("return $L.$N()", delegate(), builder.build())
                .endControlFlow()
                .build();
    }

    // void info(String message);
    default MethodSpec stringMethodBody(Level level, MethodSpec.Builder builder) {
        return builder
                .beginControlFlow("if ($L.test($T.$L))", predicate(), Level.class, level)
                .addStatement("$L.$N(msg)", delegate(), builder.build())
                .endControlFlow()
                .build();
    }

    //     public void trace(String format, Object arg1);
    default MethodSpec stringArgMethodBody(Level level, MethodSpec.Builder builder) {
        return builder
                .beginControlFlow("if ($L.test($T.$L))", predicate(), Level.class, level)
                .addStatement("$L.$N(format, arg)", delegate(), builder.build())
                .endControlFlow()
                .build();
    }

    //     public void trace(String format, Object arg1, Object arg2);
    default MethodSpec stringArg1Arg2MethodBody(Level level, MethodSpec.Builder builder) {
        return builder
                .beginControlFlow("if ($L.test($T.$L))", predicate(), Level.class, level)
                .addStatement("$L.$N(format, arg1, arg2)", delegate(), builder.build())
                .endControlFlow()
                .build();
    }

    //     public void trace(String format, Object... arguments);
    default MethodSpec stringArgArrayMethodBody(Level level, MethodSpec.Builder builder) {
        return builder
                .beginControlFlow("if ($L.test($T.$L))", predicate(), Level.class, level)
                .addStatement("$L.$N(format, arguments)", delegate(), builder.build())
                .endControlFlow()
                .build();
    }

    // public void trace(String msg, Throwable t);
    default MethodSpec stringThrowableMethodBody(Level level, MethodSpec.Builder builder) {
        return builder
                .beginControlFlow("if ($L.test($T.$L))", predicate(), Level.class, level)
                .addStatement("$L.$N(msg, t)", delegate(), builder.build())
                .endControlFlow()
                .build();
    }

    // public boolean isTraceEnabled(Marker marker);
    default  MethodSpec isEnabledMarkerMethodBody(Level level, MethodSpec.Builder builder) {
        return builder
                .beginControlFlow("if (! $L.test($T.$L))", predicate(), Level.class, level)
                .addStatement("return false", delegate(), builder.build())
                .nextControlFlow("else")
                .addStatement("return $L.$N(marker)", delegate(), builder.build())
                .endControlFlow()
                .build();
    }

    // public void trace(Marker marker, String msg);
    default  MethodSpec markerStringMethodBody(Level level, MethodSpec.Builder builder) {
        return builder
                .beginControlFlow("if ($L.test($T.$L))", predicate(), Level.class, level)
                .addStatement("$L.$N(marker, msg)",  delegate(), builder.build())
                .endControlFlow()
                .build();
    }

    // public void trace(Marker marker, String format, Object arg);
    default MethodSpec markerFormatArgMethod(Level level, MethodSpec.Builder builder) {
        return builder
                .beginControlFlow("if ($L.test($T.$L))", predicate(), Level.class, level)
                .addStatement("$L.$N(marker, format, arg)", delegate(), builder.build())
                .endControlFlow()
                .build();
    }

    //     public void trace(Marker marker, String format, Object arg1, Object arg2);
    default MethodSpec markerFormatArg1Arg2MethodBody(Level level, MethodSpec.Builder builder) {
        return builder
                .beginControlFlow("if ($L.test($T.$L))", predicate(), Level.class, level)
                .addStatement("$L.$N(marker, format, arg1, arg2)", delegate(), builder.build())
                .endControlFlow()
                .build();
    }

    //     public void trace(Marker marker, String format, Object... argArray);
    default MethodSpec markerFormatArgArrayMethodBody(Level level, MethodSpec.Builder builder) {
        return builder
                .beginControlFlow("if ($L.test($T.$L))", predicate(), Level.class, level)
                .addStatement("$L.$N(marker, format, argArray)", delegate(), builder.build())
                .endControlFlow()
                .build();
    }

    // public void trace(Marker marker, String msg, Throwable t);
    default MethodSpec markerMsgThrowableMethodBody(Level level, MethodSpec.Builder builder) {
        return builder
                .beginControlFlow("if ($L.test($T.$L))", predicate(), Level.class, level)
                .addStatement("$L.$N(marker, msg, t)", delegate(), builder.build())
                .endControlFlow()
                .build();
    }
}

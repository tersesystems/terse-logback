package com.tersesystems.logback.censor;

import com.fasterxml.jackson.core.JsonGenerator;

public class CensoringPrettyPrintingJsonGeneratorDecorator extends CensoringJsonGeneratorDecorator {
    @Override
    public JsonGenerator decorate(JsonGenerator generator) {
        return super.decorate(generator.useDefaultPrettyPrinter());
    }
}


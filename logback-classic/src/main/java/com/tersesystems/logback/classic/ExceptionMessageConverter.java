package com.tersesystems.logback.classic;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;

import java.util.List;
import java.util.Optional;

public class ExceptionMessageConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        Integer depth = getDepth();
        String prefix = getPrefix();
        String sep = getSeparator();
        String suffix = getSuffix();
        IThrowableProxy ex = event.getThrowableProxy();
        if (ex == null) {
            return "";
        }
        return processException(ex, (depth), prefix, sep, suffix);
    }

    protected Integer getDepth() {
        return Integer.parseInt(getOption(0).orElse("1"));
    }

    protected String getPrefix() {
        return getOption(1).orElse(" [");
    }

    protected String getSeparator() {
        return getOption(2).orElse(" > ");
    }

    protected String getSuffix() {
        return getOption(3).orElse("]");
    }

    protected Optional<String> getOption(int index) {
        List<String> optionList = getOptionList();
        if (optionList != null && optionList.size() >= index + 1) {
            return Optional.of(optionList.get(index));
        }
        return Optional.empty();
    }

    protected String processException(IThrowableProxy throwableProxy, Integer depth,
                                      String prefix, String sep, String suffix) {
        StringBuilder b = new StringBuilder(prefix);
        IThrowableProxy ex = throwableProxy;
        for (int i = 0; i < depth; i++) {
            b.append(ex.getMessage());
            ex = ex.getCause();
            if (ex == null || i + 1 == depth) break;
            b.append(sep);
        }
        b.append(suffix);
        return b.toString();
    }

}

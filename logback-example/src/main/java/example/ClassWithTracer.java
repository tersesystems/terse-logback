/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2019 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */
package example;

public class ClassWithTracer {

    // Add tracer to the context, and return a logger that covers over the context.
    private AppLogger getContextLogger(Request request) {
        final AppContext context;
        if (request.queryStringContains("trace")) {
            context = request.context().withTracer();
        } else {
            context = request.context();
        }
        return AppLoggerFactory.create(context).getLogger(getClass());
    }

    public void doThings(Request request) {
        AppLogger logger = getContextLogger(request);

        // This class is not logged at a TRACE level, so this should not show under
        // normal circumstances...
        if (logger.isTraceEnabled()) {
            logger.trace("This log message is only shown if the request has trace in the query string!");
        }
    }

    public static void main(String[] args) {
        ClassWithTracer classWithTracer = new ClassWithTracer();

        // run it without the trace flag
        Request request = new Request("foo=bar");
        classWithTracer.doThings(request);

        // run it WITH the trace flag
        Request requestWithTrace = new Request("foo=bar&trace=on");
        classWithTracer.doThings(requestWithTrace);
    }
}


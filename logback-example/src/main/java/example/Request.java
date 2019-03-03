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

class Request {
    private final AppContext context;
    private final String queryString;

    Request(String queryString) {
        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        this.context = AppContext.create().withCorrelationId(correlationId);
        this.queryString = queryString;
    }

    public AppContext context() {
        return context;
    }

    public boolean queryStringContains(String key) {
        return (queryString.contains(key));
    }
}


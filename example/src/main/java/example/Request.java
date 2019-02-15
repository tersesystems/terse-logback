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


package example;

import com.tersesystems.logback.context.Context;
import com.tersesystems.logback.context.logstash.AbstractLogstashContext;
import org.slf4j.Marker;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

class AppContext extends AbstractLogstashContext<AppContext> {

    public static final String CORRELATION_ID = "correlationId";

    protected AppContext(Map<?, ?> entries, boolean tracer) {
        super(entries, tracer);
    }

    public static AppContext create() {
        return new AppContext(Collections.emptyMap(), false);
    }

    public static AppContext create(Object key, Object value) {
        return new AppContext(Collections.singletonMap(key, value), false);
    }

    public Optional<String> getCorrelationId() {
        return Stream.of(entries().get(CORRELATION_ID))
                .map(cid -> (String) cid)
                .findFirst();
    }

    public AppContext withCorrelationId(String correlationId) {
        return and(AppContext.create(CORRELATION_ID, correlationId));
    }

    @Override
    public AppContext withTracer() {
        return create(entries(), true);
    }

    @Override
    public AppContext and(Context<? extends Marker, ?> context) {
        boolean tracing = this.isTracingEnabled() || context.isTracingEnabled();
        Map<Object, Object> mergedEntries = new HashMap<>(this.entries());
        mergedEntries.putAll(context.entries());
        return new AppContext(mergedEntries, tracing);
    }

}

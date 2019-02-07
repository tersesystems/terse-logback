package example;

import com.tersesystems.logback.context.AbstractContext;
import com.tersesystems.logback.context.Context;
import com.tersesystems.logback.context.LogstashContext;
import com.tersesystems.logback.context.ProxyContextLoggerFactory;
import net.logstash.logback.marker.LogstashMarker;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class ClassWithContext {

    static class ObliviousToContext {
        private final Logger logger;

        public ObliviousToContext(ILoggerFactory lf) {
            this.logger = lf.getLogger(this.getClass().getName());
        }

        public void doStuff() {
            logger.info("hello world!");
        }
    }

    public static void main(String[] args) {
        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        Context<LogstashMarker> context = LogstashContext.create("correlationId", correlationId);
        ILoggerFactory loggerFactory = ProxyContextLoggerFactory.create(context);

        ObliviousToContext obliviousToContext = new ObliviousToContext(loggerFactory);
        obliviousToContext.doStuff();
    }
}

package example;

import org.junit.Test;

public class ClassWithStructuredArgumentsTest {

    @Test
    public void logValue() {
        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        ClassWithStructuredArguments classWithStructuredArguments = new ClassWithStructuredArguments();
        classWithStructuredArguments.logValue(correlationId);
    }

    @Test
    public void logNameAndValue() {
        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        ClassWithStructuredArguments classWithStructuredArguments = new ClassWithStructuredArguments();
        classWithStructuredArguments.logNameAndValue(correlationId);
    }

    @Test
    public void logNameAndValueWithFormat() {
        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        ClassWithStructuredArguments classWithStructuredArguments = new ClassWithStructuredArguments();
        classWithStructuredArguments.logNameAndValueWithFormat(correlationId);
    }
}

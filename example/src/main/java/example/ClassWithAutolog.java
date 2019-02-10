package example;

import com.tersesystems.logback.ChangeLogLevel;
import com.tersesystems.logback.annotation.AutoLog;
import com.tersesystems.logback.annotation.MyAgent;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.lang.instrument.Instrumentation;

public class ClassWithAutolog {

    private ClassWithAutolog() {
    }

    static class SomeUnloadedClass {
        private static Logger logger = LoggerFactory.getLogger(SomeUnloadedClass.class);

        @AutoLog
        private void doStuff() {
            logger.trace("doStuff tracing works!");
            System.out.println("Hello world!");
        }
    }

    // public static void main(String[] args) {
    //     Instrumentation instrumentation = ByteBuddyAgent.install();
    //     MyAgent.premain(instrumentation);

    //     ChangeLogLevel changeLogLevel = new ChangeLogLevel();
    //     changeLogLevel.changeLogLevel("example", Level.TRACE.toString());
    //     SomeUnloadedClass instrumentedClass = new SomeUnloadedClass();
    //     instrumentedClass.doStuff();
    // }
}

package example;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Main {

    static class Runner {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        public void doInfo() {
            logger.info("I like to do stuff");
        }

        public void doWarn() {
            logger.warn("I am a warning");
        }
    }

    public static void main(String[] args) {
        final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);

        Runner runner = new Runner();

        final ScheduledFuture<?> infoHandle = scheduler.scheduleAtFixedRate(runner::doInfo, 1, 2, SECONDS);
        final ScheduledFuture<?> warnHandle = scheduler.scheduleAtFixedRate(runner::doWarn, 1, 1, SECONDS);
        scheduler.schedule(() -> { infoHandle.cancel(true); }, 60 * 60, SECONDS);
        scheduler.schedule(() -> { warnHandle.cancel(true); }, 60 * 60, SECONDS);
    }

}

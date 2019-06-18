package example;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    static class Runner {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        public void doInfo() {
            logger.info("I like to do stuff");
        }

        public void doWarn() {
            logger.warn("I like to do stuff");
        }
    }

    public static void main(String[] args) {
        Runner doStuff = new Runner();
        for(int i = 0; i < 1000; i++) {
            doStuff.doInfo();
            if (i % 2 == 0) doStuff.doWarn();
        }
    }

}

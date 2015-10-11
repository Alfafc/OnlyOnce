import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alfascompany.thread.ThreadPool;

public class ThreadsTest {

    private static final Logger logger = LoggerFactory.getLogger(ThreadsTest.class);

    public static void main(String[] args) {

//        SLF4JBridgeHandler.removeHandlersForRootLogger();
//        SLF4JBridgeHandler.install();

        final long startMillis = System.currentTimeMillis();

        final int[] i = {0};
        final ThreadPool threadPool = new ThreadPool(25);

        for (int j = 0; j < 50; j++) {
            threadPool.processInPool(() -> {

                logger.debug((i[0]++) + " print " + Thread.currentThread().getName());
//                logger.debug("Elapsed time " + (System.currentTimeMillis() - startMillis));
                try {
                    Thread.sleep(10000l);
                } catch (InterruptedException e) {

                }
            });
            logger.debug("Start to run " + j);
        }

        logger.debug("Waiting");

        threadPool.waitUntilFinish();

        logger.debug("TERMINO");
    }
}



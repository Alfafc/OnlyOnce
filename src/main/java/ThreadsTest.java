import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alfascompany.thread.ThreadPool;

public class ThreadsTest {

    private static final Logger logger = LoggerFactory.getLogger(ThreadsTest.class);

    public static void main(String[] args) {

        final long startMillis = System.currentTimeMillis();

        final int[] i = {0};
        final ThreadPool threadPool = new ThreadPool(50);

        for (int j = 0; j < 50; j++) {
            threadPool.processInPool(() -> {
                try {
                    Thread.sleep(1000l);
                } catch (InterruptedException e) {

                }

                logger.debug((i[0]++) + " print " + Thread.currentThread().getName());
                logger.debug("Elapsed time " + (System.currentTimeMillis() - startMillis));
            });
            logger.debug("Start to run " + j);
        }

        threadPool.waitUntilFinish();

        logger.debug("TERMINO");
    }
}



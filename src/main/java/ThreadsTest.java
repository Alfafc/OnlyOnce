import utils.ThreadPool;

public class ThreadsTest {

    public static void main(String[] args) {

        final long startMillis = System.currentTimeMillis();

        final int[] i = {0};
        final ThreadPool threadPool = new ThreadPool(50);

        for (int j = 0; j < 50; j++) {
            threadPool.processInPool(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000l);
                    } catch (InterruptedException e) {

                    }

                    System.err.println((i[0]++) + " print " + Thread.currentThread().getName());
                    System.err.println("Elapsed time " + (System.currentTimeMillis() - startMillis));
                }
            });
            System.err.println("Start to run " + j);
        }

        threadPool.waitUntilFinish();

        System.err.println("TERMINO");
    }
}



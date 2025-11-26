import java.util.concurrent.atomic.AtomicInteger;

// prove that its lazy
public class ThreadSafeLoggerLazy {
    private static volatile ThreadSafeLoggerLazy logger;
    public static AtomicInteger  constant= new AtomicInteger(0);

    private ThreadSafeLoggerLazy(){
        constant.incrementAndGet();
    }

    public static ThreadSafeLoggerLazy getLogger() {
        if (logger == null) {
            synchronized (ThreadSafeLoggerLazy.class) {
                if (logger == null) {
                    logger = new ThreadSafeLoggerLazy();
                }
            }
        }
        return logger;
    }


}

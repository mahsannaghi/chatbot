
// prove that its lazy
public class LazyLogger {
    private static LazyLogger logger;

    private LazyLogger(){
        System.out.println("Constructor called!");
    }

    public static LazyLogger getLogger() {
        if (logger == null)
            logger= new LazyLogger();
        return logger;
    }


}

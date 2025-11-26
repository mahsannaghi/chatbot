
// prove that its eager
public class EagerLogger {
    private static final EagerLogger EAGER_LOGGER = new EagerLogger();

    private EagerLogger(){

        System.out.println("Constructor called!");
    }

    public static EagerLogger getLogger() {
        return EAGER_LOGGER;
    }


}

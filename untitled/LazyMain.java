public class LazyMain {
    // prove that its lazy
     public static void main(String[] args) {
         System.out.println("Program started in LazyMain class");
         LazyLogger logger = LazyLogger.getLogger();
         LazyLogger logger2 = LazyLogger.getLogger();
         if (logger == logger2)
             System.out.println("yes");

     }
}

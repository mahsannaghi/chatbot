import java.util.ArrayList;
import java.util.List;

public class ThreadSafeMain {
    // prove that its thread safe
     public static void main(String[] args) throws InterruptedException {
         int n = 10;
         List<Thread> threadList= new ArrayList<>();

         for (int i=0; i<n ; i++) {
             Thread thread = new Thread(ThreadSafeLoggerLazy::getLogger);
             threadList.add(thread);
         }


         threadList.forEach(Thread::start);
         for (Thread thread : threadList) {
             thread.join();
         }

         System.out.println("Created count = " + ThreadSafeLoggerLazy.constant.get());

     }
}

package synchronization;

// Exercise 2.1: Deadlock Prevention
class DeadlockPrevention {
    private static Object resource1 = new Object();
    private static Object resource2 = new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            synchronized (resource1) {
                System.out.println("Thread 1: Holding resource 1...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                synchronized (resource2) {
                    System.out.println("Thread 1: Holding resource 1 and resource 2");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (resource1) {  // Changed to resource1
                System.out.println("Thread 2: Holding resource 1...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                synchronized (resource2) {
                    System.out.println("Thread 2: Holding resource 1 and resource 2");
                }
            }
        });

        t1.start();
        t2.start();
    }
}

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class ThreadControlAndDeadlocks {

    public static void main(String[] args) {
        System.out.println("Advanced Threading Lab\n");

        // Exercise 1: Thread interruption
        threadInterruption();

        // Exercise 2: Fork/Join framework
        forkJoinFramework();

        // Exercise 3: Deadlock scenarios
        //deadlockScenario();

        // To make the Exercise 4 run, we terminate deadlockScenario after 5 seconds
        Thread deadlockThread = new Thread(() -> deadlockScenario());
        deadlockThread.start();
        try {
            deadlockThread.join(5000); // Wait for 5 seconds max
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Moving on to next exercise...\n");

        // Exercise 4: Deadlock prevention
        deadlockPrevention();
    }

    // Exercise 1: Demonstrate thread interruption using the interrupt() method
    private static void threadInterruption() {
        System.out.println("Exercise 1: Thread Interruption");

        Thread workerThread = new Thread(() -> {
            try {
                System.out.println("Worker thread started");
                while (!Thread.currentThread().isInterrupted()) {
                    System.out.println("Working...");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                System.out.println("Worker thread interrupted");
            }
        });

        workerThread.start();

        try {
            Thread.sleep(3000);
            workerThread.interrupt();
            workerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Worker thread finished\n");
    }

    // Exercise 2: Implement a Fork/Join task for parallel processing
    private static void forkJoinFramework() {
        System.out.println("Exercise 2: Fork/Join Framework");

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        SumTask task = new SumTask(0, 1000000);

        long result = forkJoinPool.invoke(task);
        System.out.println("Sum of numbers from 0 to 1000000: " + result + "\n");
    }

    static class SumTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 10000;
        private final long start;
        private final long end;

        SumTask(long start, long end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            if (end - start <= THRESHOLD) {
                long sum = 0;
                for (long i = start; i <= end; i++) {
                    sum += i;
                }
                return sum;
            } else {
                long mid = (start + end) / 2;
                SumTask leftTask = new SumTask(start, mid);
                SumTask rightTask = new SumTask(mid + 1, end);
                leftTask.fork();
                long rightResult = rightTask.compute();
                long leftResult = leftTask.join();
                return leftResult + rightResult;
            }
        }
    }

    // Exercise 3: Create code examples that lead to deadlocks
    private static void deadlockScenario() {
        System.out.println("Exercise 3: Deadlock Scenario");

        final Object resource1 = new Object();
        final Object resource2 = new Object();

        Thread thread1 = new Thread(() -> {
            synchronized (resource1) {
                System.out.println("Thread 1: Holding resource 1...");
                try { Thread.sleep(100); } catch (InterruptedException e) {}
                System.out.println("Thread 1: Waiting for resource 2...");
                synchronized (resource2) {
                    System.out.println("Thread 1: Holding resource 1 and resource 2");
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (resource2) {
                System.out.println("Thread 2: Holding resource 2...");
                try { Thread.sleep(100); } catch (InterruptedException e) {}
                System.out.println("Thread 2: Waiting for resource 1...");
                synchronized (resource1) {
                    System.out.println("Thread 2: Holding resource 2 and resource 1");
                }
            }
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Deadlock scenario demonstration completed\n");
    }

    // Exercise 4: Implement techniques to avoid deadlocks (e.g., ordered locking)
    private static void deadlockPrevention() {
        System.out.println("Exercise 4: Deadlock Prevention");

        final ReentrantLock lock1 = new ReentrantLock();
        final ReentrantLock lock2 = new ReentrantLock();

        Thread thread1 = new Thread(() -> {
            try {
                if (lock1.tryLock(1, TimeUnit.SECONDS)) {
                    try {
                        System.out.println("Thread 1: Acquired lock1");
                        if (lock2.tryLock(1, TimeUnit.SECONDS)) {
                            try {
                                System.out.println("Thread 1: Acquired lock2");
                            } finally {
                                lock2.unlock();
                            }
                        } else {
                            System.out.println("Thread 1: Unable to acquire lock2");
                        }
                    } finally {
                        lock1.unlock();
                    }
                } else {
                    System.out.println("Thread 1: Unable to acquire lock1");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                if (lock2.tryLock(1, TimeUnit.SECONDS)) {
                    try {
                        System.out.println("Thread 2: Acquired lock2");
                        if (lock1.tryLock(1, TimeUnit.SECONDS)) {
                            try {
                                System.out.println("Thread 2: Acquired lock1");
                            } finally {
                                lock1.unlock();
                            }
                        } else {
                            System.out.println("Thread 2: Unable to acquire lock1");
                        }
                    } finally {
                        lock2.unlock();
                    }
                } else {
                    System.out.println("Thread 2: Unable to acquire lock2");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Deadlock prevention demonstration completed");
    }
}

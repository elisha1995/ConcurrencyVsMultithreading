import java.util.concurrent.*;

public class ThreadManagementLab {

    public static void main(String[] args) {
        System.out.println("Thread Management Lab\n");

        // Exercise 1: Create threads
        createThreads();

        // Exercise 2: Thread life cycle
        threadLifecycle();

        // Exercise 3: Thread synchronization
        threadSynchronization();

        // Exercise 4: Thread pools
        threadPools();
    }

    // Exercise 1: Create and start threads using Runnable and Thread
    private static void createThreads() {
        System.out.println("Exercise 1: Create Threads");

        // Using Runnable
        Runnable runnableTask = () -> {
            System.out.println("Runnable task running in " + Thread.currentThread().getName());
        };
        Thread thread1 = new Thread(runnableTask);
        thread1.start();

        // Using Thread subclass
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                System.out.println("Thread subclass running in " + Thread.currentThread().getName());
            }
        };
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    // Exercise 2: Understand thread states and lifecycle methods
    private static void threadLifecycle() {
        System.out.println("Exercise 2: Thread Life Cycle");

        Thread thread = new Thread(() -> {
            try {
                System.out.println("Thread state after start: " + Thread.currentThread().getState());
                Thread.sleep(1000);
                System.out.println("Thread state after sleep: " + Thread.currentThread().getState());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println("Thread state before start: " + thread.getState());
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Thread state after completion: " + thread.getState());
        System.out.println();
    }

    // Exercise 3: Implement basic synchronization using synchronized blocks and methods
    private static void threadSynchronization() {
        System.out.println("Exercise 3: Thread Synchronization");

        Counter counter = new Counter();
        Thread incrementThread = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        });

        Thread decrementThread = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.decrement();
            }
        });

        incrementThread.start();
        decrementThread.start();

        try {
            incrementThread.join();
            decrementThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final counter value: " + counter.getValue());
        System.out.println();
    }

    // Exercise 4: Create and use thread pools for task management
    private static void threadPools() {
        System.out.println("Exercise 4: Thread Pools");

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            executorService.submit(() -> {
                System.out.println("Task " + taskId + " executed by " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        System.out.println("All tasks completed");
    }
}

class Counter {
    private int count = 0;

    public synchronized void increment() {
        count++;
    }

    public synchronized void decrement() {
        count--;
    }

    public synchronized int getValue() {
        return count;
    }
}
import java.util.*;
import java.util.concurrent.*;

public class ConcurrencyLab {

    public static void main(String[] args) {
        // Exercise 1: Concurrency vs. Multithreading
        demonstrateConcurrencyVsMultithreading();

        // Exercise 2: Concurrent Collections
        demonstrateConcurrentCollections();

        // Exercise 3: Performance Comparison
        comparePerformance();
    }

    // Exercise 1: Demonstrate the difference between concurrency and multithreading
    private static void demonstrateConcurrencyVsMultithreading() {
        System.out.println("Exercise 1: Concurrency vs. Multithreading");

        // Multithreading example
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Thread 1: " + i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Thread 2: " + i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.println("Multithreading example:");
        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Concurrency example using ExecutorService
        System.out.println("\nConcurrency example:");
        ExecutorService executor = Executors.newFixedThreadPool(2);

        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            executor.submit(() -> System.out.println("Task " + taskId + " executed by " + Thread.currentThread().getName()));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Exercise 2: Use concurrent collections in practical scenarios
    private static void demonstrateConcurrentCollections() {
        System.out.println("\nExercise 2: Concurrent Collections");

        // ConcurrentHashMap example
        Map<String, Integer> concurrentMap = new ConcurrentHashMap<>();
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    String key = "Key" + (j % 10);
                    concurrentMap.compute(key, (k, v) -> (v == null) ? 1 : v + 1);
                }
            });
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("ConcurrentHashMap result: " + concurrentMap);

        // CopyOnWriteArrayList example
        List<String> copyOnWriteList = new CopyOnWriteArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 3; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < 5; j++) {
                    copyOnWriteList.add("Item " + j);
                    System.out.println(Thread.currentThread().getName() + " added: Item " + j);
                }
            });
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("CopyOnWriteArrayList result: " + copyOnWriteList);
    }

    // Exercise 3: Compare concurrent and non-concurrent collections in terms of performance
    private static void comparePerformance() {
        System.out.println("\nExercise 3: Performance Comparison");

        int numThreads = 10;
        int numOperations = 100000;

        // Test HashMap
        Map<Integer, Integer> hashMap = new HashMap<>();
        long hashMapTime = testMapPerformance(hashMap, numThreads, numOperations);
        System.out.println("HashMap time: " + hashMapTime + " ms");

        // Test ConcurrentHashMap
        Map<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        long concurrentHashMapTime = testMapPerformance(concurrentHashMap, numThreads, numOperations);
        System.out.println("ConcurrentHashMap time: " + concurrentHashMapTime + " ms");

        // Test ArrayList
        List<Integer> arrayList = new ArrayList<>();
        long arrayListTime = testListPerformance(arrayList, numThreads, numOperations);
        System.out.println("ArrayList time: " + arrayListTime + " ms");

        // Test CopyOnWriteArrayList
        List<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        long copyOnWriteArrayListTime = testListPerformance(copyOnWriteArrayList, numThreads, numOperations);
        System.out.println("CopyOnWriteArrayList time: " + copyOnWriteArrayListTime + " ms");
    }

    private static long testMapPerformance(Map<Integer, Integer> map, int numThreads, int numOperations) {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < numOperations; j++) {
                    map.put(j, j);
                    map.get(j);
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return System.currentTimeMillis() - startTime;
    }

    private static long testListPerformance(List<Integer> list, int numThreads, int numOperations) {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < numOperations; j++) {
                    list.add(j);
                    if (!list.isEmpty()) {
                        list.get(list.size() - 1);
                    }
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return System.currentTimeMillis() - startTime;
    }
}
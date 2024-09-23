package producerConsumerPattern;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;

public class ImprovedBlockingQueueProducerConsumer {
    private static final int BUFFER_SIZE = 5;
    private static final BlockingQueue<Integer> buffer = new LinkedBlockingQueue<>(BUFFER_SIZE);
    private static final AtomicInteger producedCount = new AtomicInteger(0);
    private static final AtomicInteger consumedCount = new AtomicInteger(0);
    private static final Random random = new Random();

    static class Producer implements Runnable {
        @Override
        public void run() {
            int value = 0;
            int retryCount = 0;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (value % 10 == 0 && random.nextBoolean()) {
                        throw new RuntimeException("Simulated error in producer");
                    }
                    buffer.put(value);
                    System.out.println("Produced: " + value);
                    producedCount.incrementAndGet();
                    value++;
                    retryCount = 0;
                    Thread.sleep(100);  // Simulate some work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (RuntimeException e) {
                    System.err.println("Producer error: " + e.getMessage());
                    retryCount++;
                    if (retryCount > 3) {
                        System.err.println("Producer skipping value after multiple retries: " + value);
                        value++;
                        retryCount = 0;
                    }
                    try {
                        Thread.sleep(200 * retryCount);  // Backoff strategy
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    static class Consumer implements Runnable {
        @Override
        public void run() {
            int retryCount = 0;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    int value = buffer.take();
                    if (value % 15 == 0 && random.nextBoolean()) {
                        throw new RuntimeException("Simulated error in consumer");
                    }
                    System.out.println("Consumed: " + value);
                    consumedCount.incrementAndGet();
                    retryCount = 0;
                    Thread.sleep(200);  // Simulate some work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (RuntimeException e) {
                    System.err.println("Consumer error: " + e.getMessage());
                    retryCount++;
                    if (retryCount > 3) {
                        System.err.println("Consumer skipping value after multiple retries");
                        retryCount = 0;
                    }
                    try {
                        Thread.sleep(200 * retryCount);  // Backoff strategy
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Thread producerThread = new Thread(new Producer());
        Thread consumerThread = new Thread(new Consumer());

        long startTime = System.nanoTime();

        producerThread.start();
        consumerThread.start();

        try {
            Thread.sleep(5000);  // Run for 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        producerThread.interrupt();
        consumerThread.interrupt();

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;  // Convert to milliseconds

        System.out.println("BlockingQueue Producer-Consumer Performance:");
        System.out.println("Duration: " + duration + " ms");
        System.out.println("Items produced: " + producedCount.get());
        System.out.println("Items consumed: " + consumedCount.get());
        System.out.println("Throughput: " + (producedCount.get() * 1000.0 / duration) + " items/second");
    }
}
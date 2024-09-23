package producerConsumerPattern;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueProducerConsumer {
    private static final int BUFFER_SIZE = 5;
    private static final BlockingQueue<Integer> buffer = new LinkedBlockingQueue<>(BUFFER_SIZE);

    static class Producer implements Runnable {
        @Override
        public void run() {
            int value = 0;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    buffer.put(value);
                    System.out.println("Produced: " + value);
                    value++;
                    Thread.sleep(100);  // Simulate some work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    static class Consumer implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    int value = buffer.take();
                    System.out.println("Consumed: " + value);
                    Thread.sleep(200);  // Simulate some work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static void main(String[] args) {
        Thread producerThread = new Thread(new Producer());
        Thread consumerThread = new Thread(new Consumer());

        producerThread.start();
        consumerThread.start();

        try {
            Thread.sleep(5000);  // Run for 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        producerThread.interrupt();
        consumerThread.interrupt();
    }
}

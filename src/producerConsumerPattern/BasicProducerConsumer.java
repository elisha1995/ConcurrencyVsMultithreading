package producerConsumerPattern;

import java.util.LinkedList;
import java.util.Queue;

public class BasicProducerConsumer {
    private static final int BUFFER_SIZE = 5;
    private static final Queue<Integer> buffer = new LinkedList<>();

    static class Producer implements Runnable {
        @Override
        public void run() {
            int value = 0;
            while (true) {
                synchronized (buffer) {
                    while (buffer.size() == BUFFER_SIZE) {
                        try {
                            buffer.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    System.out.println("Produced: " + value);
                    buffer.add(value++);
                    buffer.notifyAll();
                }
                try {
                    Thread.sleep(100);  // Simulate some work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    static class Consumer implements Runnable {
        @Override
        public void run() {
            while (true) {
                synchronized (buffer) {
                    while (buffer.isEmpty()) {
                        try {
                            buffer.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    int value = buffer.poll();
                    System.out.println("Consumed: " + value);
                    buffer.notifyAll();
                }
                try {
                    Thread.sleep(200);  // Simulate some work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
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

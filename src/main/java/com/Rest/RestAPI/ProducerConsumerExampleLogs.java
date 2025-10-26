package com.Rest.RestAPI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.File;
import java.io.IOException;

public class ProducerConsumerExampleLogs {

    private final int capacity;
    private final Queue<Integer> queue;
    public ArrayList<Integer> oddNumbers = new ArrayList<>();
    public ArrayList<Integer> evenNumbers = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(ProducerConsumerExampleLogs.class.getName());

    static {
        try {
            File dir = new File("logs");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileHandler fh = new FileHandler("logs/producer_consumer.log", true);
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);
            LOGGER.setUseParentHandlers(false);
            LOGGER.setLevel(Level.INFO);
        } catch (IOException e) {
            System.err.println("Failed to initialize file logging: " + e.getMessage());
        }
    }

    private static synchronized void log(String message) {
        System.out.println(message);
        LOGGER.info(message);
    }

    public ProducerConsumerExampleLogs(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
    }

    // Producer Method
    public synchronized void produce(int value) throws InterruptedException {
        while (queue.size() == capacity) {
            log("Queue full → Producer waiting...");
            wait();
        }

        queue.add(value);
        log(Thread.currentThread().getName() + " Produced: " + value);
        notifyAll();
    }

    // Consumer Method (classification here)
    public synchronized int consume() throws InterruptedException {
        while (queue.isEmpty()) {
            log("Queue empty → Consumer waiting...");
            wait();
        }

        int value = queue.poll();

        if (value % 2 == 0) {
            evenNumbers.add(value);
        } else {
            oddNumbers.add(value);
        }

        log("Consumer Consumed: " + value);
        notifyAll();
        return value;
    }

    public static void main(String[] args) {
        ProducerConsumerExampleLogs buffer = new ProducerConsumerExampleLogs(5);

        // Producer 1 → even numbers
        Thread t1 = new Thread(() -> {
            for (int i = 0; i <= 100; i += 2) {
                try {
                    buffer.produce(i);
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
            }
        }, "EvenProducer");

        // Producer 2 → odd numbers
        Thread t2 = new Thread(() -> {
            for (int i = 1; i <= 100; i += 2) {
                try {
                    buffer.produce(i);
                    Thread.sleep(150);
                } catch (InterruptedException ignored) {}
            }
        }, "OddProducer");

        // Consumer → all numbers
        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    buffer.consume();
                    Thread.sleep(110);
                } catch (InterruptedException ignored) {}
            }
        }, "Consumer");

        t1.start();
        t2.start();
        consumer.start();

        try {
            t1.join();
            t2.join();
            consumer.join();
        } catch (InterruptedException ignored) {}

        log("\n=================================");
        log("Final Odd Numbers: " + buffer.oddNumbers);
        log("Final Even Numbers: " + buffer.evenNumbers);
        log("=================================");
    }
}

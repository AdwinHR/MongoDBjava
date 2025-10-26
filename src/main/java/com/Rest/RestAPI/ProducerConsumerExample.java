package com.Rest.RestAPI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class ProducerConsumerExample {

    private final int capacity;
    private final Queue<Integer> queue;
    public ArrayList<Integer> oddNumbers = new ArrayList<>();
    public ArrayList<Integer> evenNumbers = new ArrayList<>();

    public ProducerConsumerExample(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
    }

    // Producer Method
    public synchronized void produce(int value) throws InterruptedException {
        while (queue.size() == capacity) {
            System.out.println("Queue full → Producer waiting...");
            wait();
        }

        queue.add(value);
        System.out.println(Thread.currentThread().getName() + " Produced: " + value);
        notifyAll();
    }

    // Consumer Method (classification here)
    public synchronized int consume() throws InterruptedException {
        while (queue.isEmpty()) {
            System.out.println("Queue empty → Consumer waiting...");
            wait();
        }

        int value = queue.poll();

        if (value % 2 == 0) {
            evenNumbers.add(value);
        } else {
            oddNumbers.add(value);
        }

        System.out.println("Consumer Consumed: " + value);
        notifyAll();
        return value;
    }

    public static void main(String[] args) {
        ProducerConsumerExample buffer = new ProducerConsumerExample(5);

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

        System.out.println("\n=================================");
        System.out.println("Final Odd Numbers: " + buffer.oddNumbers);
        System.out.println("Final Even Numbers: " + buffer.evenNumbers);
        System.out.println("=================================");
    }
}

package com.comma.learnabout;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Created by fanqi on 2018/6/27.
 * Description:
 *
 *
 1. 你可以使用wait和notify函数来实现线程间通信。你可以用它们来实现多线程（>3）之间的通信。

 2. 永远在synchronized的函数或对象里使用wait、notify和notifyAll，不然Java虚拟机会生成 IllegalMonitorStateException。

 3. 永远在while循环里而不是if语句下使用wait。这样，循环会在线程睡眠前后都检查wait的条件，并在条件实际上并未改变的情况下处理唤醒通知。

 4. 永远在多线程间共享的对象（在生产者消费者模型里即缓冲区队列）上使用wait。

 5. 基于前文提及的理由，更倾向用 notifyAll()，而不是 notify()。


 ***********
 * 这是关于Java里如何使用wait, notify和notifyAll的所有重点啦。你应该只在你知道自己要做什么的情况下使用这些函数，
 * 不然Java里还有很多其它的用来解决同步问题的方案。例如，如果你想使用生产者消费者模型的话，你也可以使用BlockingQueue，
 * 它会帮你处理所有的线程安全问题和流程控制。如果你想要某一个线程等待另一个线程做出反馈再继续运行，
 * 你也可以使用CycliBarrier或者CountDownLatch。如果你只是想保护某一个资源的话，你也可以使用Semaphore。
 * ***
 *
 3. 为什么在执行wait, notify时，必须获得该对象的锁？

 这是因为，如果没有锁，wait和notify有可能会产生竞态条件(Race Condition)。考虑以下生产者和消费者的情景：

 1.1生产者检查条件（如缓存满了）-> 1.2生产者必须等待

 2.1消费者消费了一个单位的缓存 -> 2.2重新设置了条件（如缓存没满） -> 2.3调用notifyAll()唤醒生产者

 我们希望的顺序是： 1.1->1.2->2.1->2.2->2.3

 但在多线程情况下，顺序有可能是 1.1->2.1->2.2->2.3->1.2。也就是说，在生产者还没wait之前，消费者就已经notifyAll了，这样的话，生产者会一直等下去。

 所以，要解决这个问题，必须在wait和notifyAll的时候，获得该对象的锁，以保证同步。

 请看以下利用wait，notify实现的一个生产者、一个消费者和一个单位的缓存的简单模型：


 ========================================================
 在这里复习一下Queue队列的使用：Queue使用时要尽量避免Collection的add()和remove()方法，而是要使用offer()来加入元素，使用poll()来获取并移出元素。
 它们的优点是通过返回值可以判断成功与否，add()和remove()方法在失败的时候会抛出异常。
 如果要使用前端而不移出该元素，使用element()或者peek()方法。

 */

public class ObjLearn {

    public static void main(String[] args){

        Queue<Integer> buffer = new LinkedList<>();
        int maxSize = 10;
        //多线程进行协作通信,wait的学习
        Thread producer = new LearnProducer("PRODUCER",buffer,maxSize);
        Thread consumer = new LearnConsumer("CONSUMER",buffer, maxSize);
        producer.start();
        consumer.start();

    }

    static class LearnProducer extends Thread{

        private Queue<Integer> queue;

        private int maxSize;


        public LearnProducer(String name, Queue<Integer> queue, int maxSize) {
            super(name);
            this.queue = queue;
            this.maxSize = maxSize;
        }

        @Override
        public void run() {
            while (true){
                synchronized (queue){
                    while (queue.size() == maxSize) {
                        try {
                            System.out .println("Queue is full, " + "Producer thread waiting for " + "consumer to take something from queue");
                            queue.wait();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    Random random = new Random();
                    int i = random.nextInt();
                    System.out.println("Producing value : " + i);
                    queue.add(i);
                    queue.notifyAll();
                }
            }
        }

    }


    static class LearnConsumer extends Thread{

        private Queue<Integer> queue;
        private int maxSize;

        public LearnConsumer(String name, Queue<Integer> queue, int maxSize) {
            super(name);
            this.queue = queue;
            this.maxSize = maxSize;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        System.out.println("Queue is empty," + "Consumer thread is waiting" + " for producer thread to put something in queue");
                        try {
                            queue.wait();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    System.out.println("Consuming value : " + queue.poll());
                    queue.notifyAll();
                }
            }
        }
    }

}

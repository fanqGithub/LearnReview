package com.comma.learnabout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by fanqi on 2018/6/26.
 * Description:
 * java线程协作：wait notify notifyall
 *  （1）wait方法 线程自动释放其占有的对象锁，并等待notify
        调用线程的sleep，yield方法时，线程并不会让出对象锁，wait却不同。
        wait函数必须在同步代码块中调用(也就是当前线程必须持有对象的锁)
    （2）notify和notifyAll方法
        notify方法只会唤醒一个正在等待的线程(至于唤醒谁，不确定！)，而notifyAll方法会唤醒所有正在等待的线程。
        还有一点需要特别强调：调用notify和notifyAll方法后，当前线程并不会立即放弃锁的持有权，而必须要等待当前同步代码块执行完才会让出锁
    （3）如果一个对象之前没有调用wait方法，那么调用notify方法是没有任何影响的。
 */

public class ObjWaitNotifyLearn {

    public static Object object = new Object();

    static class Thread1 implements Runnable {
        @Override
        public void run() {
            synchronized(object) {
                System.out.println(Thread.currentThread().getName()+" is running.");
                try {
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName()+" get the lock.");
            }
        }
    }

    static class Thread2 implements Runnable {
        @Override
        public void run() {
            synchronized(object) {
                System.out.println(Thread.currentThread().getName()+" is running.");
                object.notify();
                System.out.println(Thread.currentThread().getName()+" invoke notify()");
                System.out.println(Thread.currentThread().getName()+" release the lock.");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
//        Thread thread1 = new Thread(new Thread1());
//        Thread thread2 = new Thread(new Thread2());
//        thread1.start();
//        TimeUnit.SECONDS.sleep(1);
//        thread2.start();

        Producer p = new Producer();
        p.start();
        new Consumer("Consumer1", p).start();
        new Consumer("Consumer2", p).start();
        new Consumer("Consumer3", p).start();

    }

    static class MyMessage{
        private String msg;

        public MyMessage() {
        }

        public MyMessage(String msg) {
            this.msg = msg;
        }
    }

    //另一种体验方式(生产者和消费者模式)
    //生产
    static class Producer extends Thread{
        List<MyMessage> msgList = new ArrayList<>();

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(3000);
                    System.out.println("开始生产了一个message....");
                    MyMessage msg = new MyMessage();
                    synchronized(msgList) {
                        msgList.add(msg);
                        //这里只能是notify而不能是notifyAll，否则remove(0)会报java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
                        msgList.notify();
                        System.out.println("notify,线程们注意了，有新msg可以使用....");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public MyMessage waitMsg() {
            synchronized(msgList) {
                if(msgList.size() == 0) {
                    try {
                        msgList.wait();
                        System.out.println("wait,生产队列空了，请所有线程进入等待....");
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return msgList.remove(0);
            }
        }
    }

    //消费
    static class Consumer extends Thread{
        private Producer mProducer;
        public Consumer(String name, Producer mProducer) {
            super(name);
            this.mProducer = mProducer;
        }

        @Override
        public void run() {
            while (true) {
                MyMessage msg = mProducer.waitMsg();
                System.out.println("我是" + getName() + "，我get a msg："+"\n");
            }
        }
    }


}

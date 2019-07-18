package com.feng.learn;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingQ {

    private Object notEmpty = new Object();
    private Object notFull = new Object();
    private Queue<Object> linkedList = new LinkedList<Object>();
    private int maxLength = 1;

    public Object take() throws InterruptedException {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": inside take.");
        Object ret;
        synchronized (notEmpty) {
            System.out.println(threadName + ": notEmpty acquired.");
            if (linkedList.size() == 0) {
                System.out.println(threadName + ": notEmpty wait start.");
                notEmpty.wait();
                System.out.println(threadName + ": notEmpty wait end.");
            }
            synchronized (notFull) {
                System.out.println(threadName + ": notFull acquired.");
                if (linkedList.size() == maxLength) {
                    notFull.notifyAll();
                }
                ret = linkedList.poll();
            }
            System.out.println(threadName + ": notFull released.");
        }
        System.out.println(threadName + ": notEmpty released.");
        return ret;
    }

    public void put(Object object) throws InterruptedException {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": inside put.");
        synchronized (notEmpty) {
            System.out.println(threadName + ": notEmpty acquired.");
            if (linkedList.size() == 0) {
                notEmpty.notifyAll();
            }
            synchronized (notFull) {
                System.out.println(threadName + ": notFull acquired.");
                if (linkedList.size() == maxLength) {
                    System.out.println(threadName + ": notFull wait start.");
                    notFull.wait();
                    System.out.println(threadName + ": notFull wait end.");
                }
                linkedList.add(object);
            }
            System.out.println(threadName + ": notFull released.");
        }
        System.out.println(threadName + ": notEmpty released.");
    }

}
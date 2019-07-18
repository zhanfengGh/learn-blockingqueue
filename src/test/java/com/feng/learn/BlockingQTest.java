package com.feng.learn;

import org.junit.Test;

public class BlockingQTest {

    @Test
    public void test() throws InterruptedException {
        BlockingQ queue = new BlockingQ();

        Runnable putTask = () -> {
            try {
                queue.put(new Object());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable takeTask = () -> {
            try {
                queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        new Thread(putTask, "A").start();
        new Thread(putTask, "B").start();
        new Thread(putTask, "C").start();

        Thread.sleep(1000); // 等待1s，让 if (linkedList.size() == maxLength) 条件成立

        new Thread(takeTask, "D").start();

        Thread.currentThread().join(); //防止线程被junit kill

    }

}

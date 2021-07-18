package com.sk.e01.adddemo;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class MyAdd {
    private volatile Integer value = null;
    private Semaphore semaphore  = new Semaphore(1);
    private CyclicBarrier cyclicBarrier;
    private CountDownLatch countDownLatch;

    public static int add32() {
        return 3 + 2;
    }

    synchronized public int getVal() throws InterruptedException {
        while (this.value == null) {
            wait();
        }
        return this.value;
    }

    synchronized public void setVal(int val) {
        this.value = val;
        notifyAll();
    }

    public void setSemaphoreVal(int val) throws InterruptedException {
        this.semaphore.acquire();
        this.value = val;
        this.semaphore.release();
    }

    public int getSemaphoreVal() throws InterruptedException {
        semaphore.acquire();
        int ret = this.value;
        semaphore.release();
        return ret;
    }

    public void setCyclicBarrier(CyclicBarrier cyclicBarrier) {
        this.cyclicBarrier = cyclicBarrier;
    }

    public void setCyclicBarrierVal(int val) throws BrokenBarrierException, InterruptedException {
        this.value = val;
        this.cyclicBarrier.await();
    }

    public int getCyclicBarrierVal() {
        return this.value;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public void setCountDownLatchVal(int val) {
        this.value = val;
        this.countDownLatch.countDown();
    }

    public int getCountDownLatchVal() throws InterruptedException {
        this.countDownLatch.await();
        return this.value;
    }
}

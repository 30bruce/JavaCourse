package com.sk.e01;

import com.sk.e01.adddemo.MyAdd;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class ByCyclicBarrier {
    public static void main(String[] args) {
        MyAdd myAdd = new MyAdd();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(1, () -> {
            int ret = myAdd.getCyclicBarrierVal();
            System.out.println("获得子线程返回值为：" + ret);
        });
        myAdd.setCyclicBarrier(cyclicBarrier);

        Thread thread = new Thread(() -> {
            try {
                myAdd.setCyclicBarrierVal(5);
            } catch (BrokenBarrierException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }
}

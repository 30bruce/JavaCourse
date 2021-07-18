package com.sk.e01;

import com.sk.e01.adddemo.MyAdd;

import java.util.concurrent.atomic.AtomicInteger;

public class ByThreadJoin {
    public static void main(String[] args) throws InterruptedException {
        AtomicInteger atomicInteger = new AtomicInteger();
        Thread thread = new Thread(() -> {
            atomicInteger.set(MyAdd.add32());
        });
        thread.start();
        thread.join();

        int ret = atomicInteger.get();
        System.out.println("获得子线程返回值为：" + ret);
    }
}

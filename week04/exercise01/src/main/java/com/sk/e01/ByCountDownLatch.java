package com.sk.e01;

import com.sk.e01.adddemo.MyAdd;

import java.util.concurrent.CountDownLatch;

public class ByCountDownLatch {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        MyAdd myAdd = new MyAdd();
        myAdd.setCountDownLatch(countDownLatch);
        Thread thread = new Thread(() -> {
            myAdd.setCountDownLatchVal(5);
        });
        thread.start();

        int ret = myAdd.getCountDownLatchVal();
        System.out.println("获得子线程返回值为：" + ret);
    }
}

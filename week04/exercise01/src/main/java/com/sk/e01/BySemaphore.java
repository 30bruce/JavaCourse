package com.sk.e01;

import com.sk.e01.adddemo.MyAdd;

public class BySemaphore {
    public static void main(String[] args) throws InterruptedException {
        MyAdd myAdd = new MyAdd();
        Thread thread = new Thread(() -> {
            try {
                myAdd.setSemaphoreVal(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread.start();

        int ret = myAdd.getSemaphoreVal();
        System.out.println("获得子线程返回值为：" + ret);
    }
}

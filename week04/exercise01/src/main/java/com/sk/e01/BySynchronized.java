package com.sk.e01;

import com.sk.e01.adddemo.MyAdd;

public class BySynchronized {
    public static void main(String[] args) throws InterruptedException {
        MyAdd myAdd = new MyAdd();
        Thread thread  = new Thread(() -> {
            myAdd.setVal(5);
        });

        thread.start();
        int ret = myAdd.getVal();
        System.out.println("获得子线程返回值为：" + ret);
    }
}

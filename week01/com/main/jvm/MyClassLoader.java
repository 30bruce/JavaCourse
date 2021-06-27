package com.main.jvm;

import java.io.IOException;
import java.io.InputStream;
import java.io.Closeable;
import java.lang.reflect.Method;

/*
自定义一个 ClassLoader，加载一个 Hello.xlass 文件，执行 hello 方法，此文件内容是一个 Hello.class 文件所有字节（x=255-x）处理后的文件
 */
public class MyClassLoader extends ClassLoader {
    public static void main(String[] args) throws Exception {
        String className = "Hello";
        String methodName = "hello";

        MyClassLoader classLoader = new MyClassLoader();
        Class<?> cls = classLoader.findClass(className);
        Object instance = cls.newInstance();
        // 获得方法对象
        Method method =  cls.getMethod(methodName);
        // 实例方法调用
        method.invoke(instance);
    }

    public Class<?> findClass(String name) throws ClassNotFoundException {
        String classFilepath = "com/main/resources/" + name + ".xlass";
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(classFilepath);
        try {
            byte[] byteArr = new byte[inputStream.available()];
            inputStream.read(byteArr);
            byteArr = decode(byteArr);
            return defineClass(name, byteArr, 0, byteArr.length);
        } catch (IOException | NullPointerException e) {
            throw new ClassNotFoundException(name, e);
        } finally {
            close(inputStream);
        }
    }

    // 将处理后的字节数组解码成原来的字节数组
    public static byte[] decode(byte[] byteArr) {
        int byteLen = byteArr.length;
        byte[] resArr = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            resArr[i] = (byte) (255 - byteArr[i]);
        }
        return resArr;
    }

    // 资源关闭
    private static void close(Closeable res) {
        if (res != null) {
            try {
                res.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

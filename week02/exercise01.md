#### 测试文件源码
```Java
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
/*
演示GC日志生成与解读
*/
public class GCLogAnalysis {
    private static Random random = new Random();
    public static void main(String[] args) {
        // 当前毫秒时间戳
        long startMillis = System.currentTimeMillis();
        // 持续运行毫秒数; 可根据需要进行修改
        long timeoutMillis = TimeUnit.SECONDS.toMillis(1);
        // 结束时间戳
        long endMillis = startMillis + timeoutMillis;
        LongAdder counter = new LongAdder();
        System.out.println("正在执行...");
        // 缓存一部分对象; 进入老年代
        int cacheSize = 2000;
        Object[] cachedGarbage = new Object[cacheSize];
        // 在此时间范围内,持续循环
        while (System.currentTimeMillis() < endMillis) {
            // 生成垃圾对象
            Object garbage = generateGarbage(100*1024);
            counter.increment();
            int randomIndex = random.nextInt(2 * cacheSize);
            if (randomIndex < cacheSize) {
                cachedGarbage[randomIndex] = garbage;
            }
        }
        System.out.println("执行结束!共生成对象次数:" + counter.longValue());
    }

    // 生成对象
    private static Object generateGarbage(int max) {
        int randomSize = random.nextInt(max);
        int type = randomSize % 4;
        Object result = null;
        switch (type) {
            case 0:
                result = new int[randomSize];
                break;
            case 1:
                result = new byte[randomSize];
                break;
            case 2:
                result = new double[randomSize];
                break;
            default:
                StringBuilder builder = new StringBuilder();
                String randomString = "randomString-Anything";
                while (builder.length() < randomSize) {
                    builder.append(randomString);
                    builder.append(max);
                    builder.append(randomSize);
                }
                result = builder.toString();
                break;
        }
        return result;
    }
}
```
#### 测试环境
- OS： macOS 11.x
- 物理内存：16GB  
- jdk 版本： jdk1.8


### 运行垃圾回收器
#### 串行 GC 
`java -XX:+UseSerialGC -XX:+PrintGCDetails -Xmx128m -Xms128m GCLogAnalysis`  
> 共进行 9 次 young gc， 1 次老年代 gc，18 次 full gc，程序发生 OOM  

` java -XX:+UseSerialGC -XX:+PrintGCDetails -Xmx512m -Xms512m GCLogAnalysis`   
> 9 次 young gc 后，老年代 gc 和 young gc 交替进行，STW 时间范围 10ms ~ 70ms

`java -XX:+UseSerialGC -XX:+PrintGCDetails -Xmx1g -Xms1g GCLogAnalysis`  
> 共进行 4 次 young gc， STW 时间范围 96ms ~ 164ms  

`java -XX:+UseSerialGC -XX:+PrintGCDetails -Xmx2g -Xms2g GCLogAnalysis`
> 共进行 3 次 young gc， STW 时间范围 105ms ~ 233ms

##### 总结
堆内存越来越大，发生的 young gc 次数越来越少，但是相应的 gc 暂停时长越来越长。  


#### 并行 GC 
`java -XX:+UseParallelGC  -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xmx256m -Xms256m GCLogAnalysis`  
> 执行 10 次 young gc 后执行了 21 次 full gc，程序发生 OOM  

`java -XX:+UseParallelGC  -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xmx512m -Xms512m GCLogAnalysis`  
> 共进行 21 次 young gc，3 次 full gc，STW 时间范围 5ms ~ 54ms

`java -XX:+UseParallelGC  -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xmx1g -Xms1g GCLogAnalysis`
> 共进行 10 次 young gc，STW 时间范围 30ms ~ 55ms  

`java -XX:+UseParallelGC  -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xmx2g -Xms2g GCLogAnalysis`  
> 共进行 5 次 young gc，STW 时间范围 70ms ~ 110ms  

##### 总结
堆内存越来越大，发生的 young gc 次数越来越少，相应的 gc 暂停时长越来越长。并行 GC 比串行 GC 执行暂停时长短。  

#### G1 GC 
`java -XX:+UseG1GC  -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xmx256m -Xms256m GCLogAnalysis`  
> 共进行 34 次 young gc，11 次 full gc, 程序发生 OOM    

`java -XX:+UseG1GC  -XX:+PrintGC  -Xmx512m -Xms512m GCLogAnalysis`  
> 共进行 26 次 young gc，STM 时间范围 3ms ~ 13 ms  

`java -XX:+UseG1GC  -XX:+PrintGC  -Xmx1g -Xms1g GCLogAnalysis`  
> 共进行 11 次 young gc，STM 时间范围 7ms ~ 26 ms  

`java -XX:+UseG1GC  -XX:+PrintGC  -Xmx2g -Xms2g GCLogAnalysis`  
> 共进行 14 次 young gc，STM 时间范围 13ms ~ 34 ms   

##### 总结
堆内存越来越大，相应的 gc 暂停时长越来越长，但是 gc 次数并没有线性下降，降到一定程度后稳定在一个区间。G1 GC 比并行 GC 和串行 GC 执行暂停时长都较短。   







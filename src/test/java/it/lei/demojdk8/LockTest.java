package it.lei.demojdk8;



import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

/**
 * @author huangl
 * @date 2020/9/3 16:29
 * @desciption TODO
 */
public class LockTest {
    public void doSomething(Lock lock){
        System.out.println(String.format("线程%s准备获取锁",Thread.currentThread().getName()));
        boolean b = lock.tryLock();
        if (!b){
            System.out.println(String.format("线程%s未获取到锁",Thread.currentThread().getName()));
            return;
        }
        System.out.println(String.format("线程%s获取到锁",Thread.currentThread().getName()));
        try {
            System.out.println(String.format("线程%s准备做事",Thread.currentThread().getName()));
            Thread.sleep(10*1000);
            System.out.println(String.format("线程%s做事完毕",Thread.currentThread().getName()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
    public void doSomethingWithDDl(Lock lock) throws InterruptedException {
        System.out.println(String.format("线程%s准备获取锁",Thread.currentThread().getName()));
        boolean b = lock.tryLock(15, TimeUnit.MINUTES);
        if (!b){
            System.out.println(String.format("线程%s未获取到锁",Thread.currentThread().getName()));
            return;
        }
        System.out.println(String.format("线程%s获取到锁",Thread.currentThread().getName()));
        try {
            System.out.println(String.format("线程%s准备做事",Thread.currentThread().getName()));
            Thread.sleep(10*1000);
            System.out.println(String.format("线程%s做事完毕",Thread.currentThread().getName()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
    public void doSomethingWithInterupt(Lock lock) throws InterruptedException {
        System.out.println(String.format("线程%s准备获取锁",Thread.currentThread().getName()));
        lock.lockInterruptibly();

        System.out.println(String.format("线程%s获取到锁",Thread.currentThread().getName()));
        try {
            System.out.println(String.format("线程%s准备做事",Thread.currentThread().getName()));
            Thread.sleep(10*1000);
            System.out.println(String.format("线程%s做事完毕",Thread.currentThread().getName()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
    /**
     * 锁的一般使用
     */
    @Test
    public void lockTest(){
        //默认为非公平锁 带参数可以设置为公平锁
        ReentrantLock reentrantLock = new ReentrantLock();
        Runnable task1 = new Runnable() {
            @Override
            public void run() {
                doSomething(reentrantLock);
            }
        };

        Runnable task2 = new Runnable() {
            @Override
            public void run() {
                doSomething(reentrantLock);
            }
        };
        new Thread(task1).start();
        new Thread(task2).start();
        while (true){}

    }
    @Test
    public void lockDDLTest() throws InterruptedException {
        //默认为非公平锁 带参数可以设置为公平锁
        ReentrantLock reentrantLock = new ReentrantLock();
        Runnable task1 = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                doSomethingWithDDl(reentrantLock);
            }
        };

        Runnable task2 = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                doSomethingWithDDl(reentrantLock);
            }
        };


        Thread thread1 = new Thread(task1);
        Thread thread2 = new Thread(task1);
        thread1.start();
          //  thread1.interrupt();
//        Thread.sleep(2*1000);
        thread2.start();
//        Thread.sleep(2*1000);
//        thread2.interrupt();
        while (true){}
    }
    /**
     * 以interupt的方式去获取锁
     * 如果等待线程在等待获取锁的时候被中断了就会终止等待,值得注意的是  如果线程正在运行被设置了中断标识是不会like终止等待的
     */
    @Test
    public void InteruptLockTest() throws InterruptedException {
        //默认为非公平锁 带参数可以设置为公平锁

        ReentrantLock reentrantLock = new ReentrantLock();
        Runnable task1 = new Runnable() {
            @Override
            public void run() {
                try {
                    doSomethingWithInterupt(reentrantLock);
                } catch (InterruptedException e) {
                    System.out.println("被中断了,做点其他的事情");
                    e.printStackTrace();
                } finally {
                }
            }
        };

        Thread thread = new Thread(task1);
        thread.start();
        System.out.println("休眠2s");
        Thread.sleep(2000);
        thread.interrupt();


        while (true){}
    }

    /**
     * lock的唤醒等待机制
     * @throws InterruptedException
     */
    @Test
    public void conditionTest() throws InterruptedException {
        ReentrantLock reentrantLock = new ReentrantLock();
        Condition addCondition = reentrantLock.newCondition();
        Condition removeCondition = reentrantLock.newCondition();
        ArrayList<String> targets = new ArrayList<>(10);
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                reentrantLock.lock();
                int time=0;
                while (time<100){
                    if (targets.size()==0){
                        addCondition.signal();
                        System.out.println("去生成东西吧");
                        removeCondition.await();
                    }else {
                        System.out.println("消费一个");
                        targets.remove(0);
                    }
                    time++;
                }
                System.out.println("释放锁1");
                reentrantLock.unlock();
            }
        }).start();
        Thread.sleep(1000);
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                reentrantLock.lock();
                int time=0;
                while (time<100){
                    if (targets.size()<2){
                        System.out.println("生成一个");
                        targets.add("111");
                    }else {
                        System.out.println("生成完毕,去消费吧");
                        removeCondition.signal();
                        addCondition.await();
                    }
                    time++;
                }
                System.out.println("释放锁2");
                reentrantLock.unlock();
            }
        }).start();



        while (true){

        }
    }

    /**
     * 写锁获取锁需要等其他线程释放写锁和读锁
     * 读锁获取锁需要等其他线程释放写锁
     * @throws InterruptedException
     */
    @Test
    public void readWriteTest() throws InterruptedException {
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        Lock readLock = readWriteLock.readLock();
        Lock writeLock = readWriteLock.writeLock();
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                System.out.println("写锁获取锁");
                writeLock.lock();
                System.out.println("写锁做事");
                Thread.sleep(10*1000);
                System.out.println("写锁结束");
                writeLock.unlock();
            }
        }).start();
        Thread.sleep(1*1000);
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                System.out.println("读锁1获取锁");
                readLock.lock();
                System.out.println("读锁1做事");
                Thread.sleep(10*1000);
                System.out.println("读锁1结束");
                readLock.unlock();
            }
        }).start();
        Thread.sleep(1*1000);
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                System.out.println("读锁2获取锁");
                readLock.lock();
                System.out.println("读锁2做事");
                Thread.sleep(10*1000);
                System.out.println("读锁2结束");
                readLock.unlock();
            }
        }).start();

        while (true){

        }
    }
}

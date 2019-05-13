package com.dzkandian.common.uitls;

/**
 * 基础线程对象.
 *
 * @author jevan
 * @version (1.0 at 2013-6-17)
 * @version (1.1 at 2013-7-2) 增加onDestory接口{@link #onDestory()}，增加stop方法{@link #stop() }。
 */
public abstract class BaseThread implements Runnable {
    public static final int SUSPEND_TIME_MILLISECONDS = 50;

    private String name;
    private Thread mThread;

    private boolean suspendFlag = false;// 控制线程的执行
    // private int i = 0;
    private String TAG = getName();


    /**
     * 构造函数
     * @param suspend 初始化是否暂停。
     */
    public BaseThread( boolean suspend) {
        suspendFlag = suspend;
        mThread = new Thread(this);
        System.out.println("new Thread: " + mThread);
        mThread.start();
    }


    /**
     * 构造函数
     * @param name 线程名称。
     * @param suspend 初始化是否暂停。
     */
    public BaseThread(String name, boolean suspend) {
        suspendFlag = suspend;
        this.name = name;
        mThread = new Thread(this, name);
        System.out.println("new Thread: " + mThread);
        mThread.start();
    }

    public void run() {
        try {
            while (true) {
                // System.out.println(name + ": " + i++);
                synchronized (this) {
                    while (suspendFlag) {
                        wait();
                    }
                }
                Thread.sleep(SUSPEND_TIME_MILLISECONDS);
                process();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            onDestory();
        }
    }

    /**
     * 线程处理接口。
     */
    public abstract void process();

    /**
     * 线程暂停
     */
    public void suspend() {
        this.suspendFlag = true;
    }

    /**
     * 唤醒线程
     */
    public synchronized void resume() {
        this.suspendFlag = false;
        notify();
    }

    /**
     * 返回线程名
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * 获取线程对象。
     *
     * @return 线程对象。
     */
    public Thread getT() {
        return mThread;
    }

    /**
     * 停止线程运行。
     */
    public void stop() {
        if (mThread != null){

            mThread.interrupt();
            mThread = null;
        }
    }

    /**
     * 线程处理接口。
     */
    public void onDestory()
    {
    }

}

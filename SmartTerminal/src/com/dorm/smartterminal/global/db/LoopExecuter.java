/**
 * 
 */
package com.dorm.smartterminal.global.db;

import java.util.Queue;

import com.dorm.smartterminal.global.util.LogUtil;

/**
 * @author Andy
 * 
 */
public class LoopExecuter extends Thread {

    Queue<QueryTask> queryTaskQueue = null;

    private boolean keepThreadRunning = true;

    private int SLEEP_TIME = 0;

    public LoopExecuter(Queue<QueryTask> queryTaskQueue) {

        this.queryTaskQueue = queryTaskQueue;

    }

    public synchronized void startExeceter() {

        if (!isAlive()) {

            start();
        }

        LogUtil.log(this, "start loop executer success.");

    }

    @Override
    public void run() {

        while (keepThreadRunning) {

            // block
            blockThreadWhenQueueEmpty();

            // do some thing
            executeOneQueryTask();
        }
    }

    private void executeOneQueryTask() {

        QueryTask queryTask = null;

        if ((queryTask = queryTaskQueue.poll()) != null) {

            queryTask.execute();

            LogUtil.log(this, "execute one query task success.");
        }
    }

    private void blockThreadWhenQueueEmpty() {

        try {

            // executer pause for period
            sleep(SLEEP_TIME);

            synchronized (this) {

                if (keepThreadRunning && queryTaskQueue.isEmpty()) {

                    LogUtil.log(this, "block loop executer success.");

                    wait();
                }
            }
        }
        catch (InterruptedException e) {

            e.printStackTrace();
        }
    }

    public synchronized void nextExecute() {

        notify();

        LogUtil.log(this, "notify loop executer success.");
    }

    public synchronized void stopExecuter() {

        keepThreadRunning = false;
        notify();

        LogUtil.log(this, "stop loop executer success.");
    }
}

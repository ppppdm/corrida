/**
 * 
 */
package com.dorm.smartterminal.global.db;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import com.dorm.smartterminal.global.util.LogUtil;

/**
 * @author Andy
 * 
 */
public class LoopExecuter extends Thread {

    Queue<QueryTask> queryTaskQueue = null;

    private boolean keepThreadRunning = true;

    private int HEART_BEAT_TIME = 500;

    private int currentExecutingTaskNum = 0;

    public LoopExecuter(Queue<QueryTask> queryTaskQueue) {

        this.queryTaskQueue = queryTaskQueue;

    }

    public synchronized void startExeceter() {

        if (!isAlive()) {

            start();
            startHeartBeatTask();
        }

        LogUtil.log(this, "start loop executer success.");

    }

    private void startHeartBeatTask() {

        Timer timer = new Timer();
        HeartBeatTask heartBeatTask = new HeartBeatTask(this);
        timer.schedule(heartBeatTask, 0, HEART_BEAT_TIME);

    }

    public class HeartBeatTask extends TimerTask {

        LoopExecuter loopExecuter = null;

        public HeartBeatTask(LoopExecuter loopExecuter) {

            this.loopExecuter = loopExecuter;
        }

        public void run() {

            synchronized (loopExecuter) {

                loopExecuter.notify();
            }

            LogUtil.log(this, "heart beat notify.");

        }
    };

    @Override
    public void run() {

        while (keepThreadRunning) {

            // do some thing
            executeOneQueryTask();

            // block
            blockThreadWhenQueuing();
        }
    }

    private void executeOneQueryTask() {

        QueryTask queryTask = null;

        if ((queryTask = queryTaskQueue.poll()) != null) {

            queryTask.execute();

            currentExecutingTaskNum++;

            LogUtil.log(this, "execute one query task success. [currentExecutingTaskNum : " + currentExecutingTaskNum
                    + "]");
        }
        else {

            LogUtil.log(this, "queue empty!");
        }
    }

    private void blockThreadWhenQueuing() {

        while (keepThreadRunning && (currentExecutingTaskNum > 0 || queryTaskQueue.isEmpty())) {

            LogUtil.log(this, "block loop executer success. [currentExecutingTaskNum : " + currentExecutingTaskNum
                    + "]");

            try {

                synchronized (this) {

                    wait();
                }
            }
            catch (InterruptedException e) {

                e.printStackTrace();
            }
        }
    }

    public synchronized void notifyEcecute() {

        notify();

        LogUtil.log(this, "notify loop executer add new task.");

    }

    public synchronized void nextExecute() {

        currentExecutingTaskNum--;

        if (currentExecutingTaskNum < 0) {

            currentExecutingTaskNum = 0;
        }

        notify();

        LogUtil.log(this, "notify loop executer success. [currentExecutingTaskNum : " + currentExecutingTaskNum + "]");
    }

    public synchronized void stopExecuter() {

        keepThreadRunning = false;
        notify();

        LogUtil.log(this, "stop loop executer success.");
    }
}

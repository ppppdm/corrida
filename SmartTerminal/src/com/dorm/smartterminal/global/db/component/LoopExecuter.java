/**
 * 
 */
package com.dorm.smartterminal.global.db.component;

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

    private int HEART_BEAT_TIME = 50;

    private int currentExecutingTaskNum = 0;

    private Timer heartBeatTaskTimer = null;

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

        heartBeatTaskTimer = new Timer();
        HeartBeatTask heartBeatTask = new HeartBeatTask(this);
        heartBeatTaskTimer.schedule(heartBeatTask, 0, HEART_BEAT_TIME);

    }

    private class HeartBeatTask extends TimerTask {

        LoopExecuter loopExecuter = null;

        public HeartBeatTask(LoopExecuter loopExecuter) {

            this.loopExecuter = loopExecuter;
        }

        public void run() {

            synchronized (loopExecuter) {

                loopExecuter.notify();
            }

            // LogUtil.log(this, "heart beat notify.");

        }
    };

    @Override
    public void run() {

        while (keepThreadRunning) {

            // do some thing
            executeOneQueryTask();

            // block
            blockThreadWhenQueuingOrEmpty();
        }

        cancelHeartBeatTask();
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

    private void blockThreadWhenQueuingOrEmpty() {

        while (keepThreadRunning && (currentExecutingTaskNum > 0 || queryTaskQueue.isEmpty())) {

            // LogUtil.log(this, "block loop executer success. [currentExecutingTaskNum : " + currentExecutingTaskNum
            // + "]");

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

    private void cancelHeartBeatTask() {

        heartBeatTaskTimer.cancel();
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

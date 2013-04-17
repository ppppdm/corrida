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

    private int WAITING_TIME = 500;

    private int currentExecutingTaskNum = 0;

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

            // waiting
            // waitingForExecution();

            // do some thing
            executeOneQueryTask();

            // block
            blockThreadWhenQueuing();
        }
    }

    // private void waitingForExecution() {
    //
    // if (!queryTaskQueue.isEmpty()) {
    //
    // waitingAndAutoMotifyTaskNum();
    // }
    // else {
    //
    // LogUtil.log(this, "queue empty!");
    //
    // }
    // }

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

        // try {

        // synchronized (this) {

        // if (keepThreadRunning && (currentExecutingTaskNum > 0 || queryTaskQueue.isEmpty())) {
        while (keepThreadRunning && (currentExecutingTaskNum > 0 || queryTaskQueue.isEmpty())) {

            LogUtil.log(this, "block loop executer success. [currentExecutingTaskNum : " + currentExecutingTaskNum
                    + "]");

            // waitingAndAutoMotifyTaskNum();
            try {
                
                synchronized (this) {

                    wait();
                }
            }
            catch (InterruptedException e) {

                e.printStackTrace();
            }
        }
        // }
        // }
        // catch (InterruptedException e) {
        //
        // e.printStackTrace();
        // }
    }

    private void waitingAndAutoMotifyTaskNum() {

        LogUtil.log(this, "start waiting.");

        // while (currentExecutingTaskNum > 0) {

        Timer timer = new Timer();
        WaitingTask waitingTask = new WaitingTask(this);
        timer.schedule(waitingTask, WAITING_TIME);

        try {
            synchronized (this) {

                wait();
            }
        }
        catch (InterruptedException e) {

            e.printStackTrace();
        }

        timer.cancel();

        // }

        LogUtil.log(this, "waiting finish.");
    }

    public class WaitingTask extends TimerTask {

        LoopExecuter loopExecuter = null;

        public WaitingTask(LoopExecuter loopExecuter) {

            this.loopExecuter = loopExecuter;
        }

        public void run() {

            currentExecutingTaskNum--;

            if (currentExecutingTaskNum < 0) {

                currentExecutingTaskNum = 0;
            }

            synchronized (loopExecuter) {

                loopExecuter.notify();
            }

            LogUtil.log(this, "auto delete one task. [currentExecutingTaskNum : " + currentExecutingTaskNum + "]");

        }

        // @Override
        // public boolean cancel() {
        //
        // LogUtil.log(this, "waiting timer canceled");
        //
        // return super.cancel();
        // }
    };

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

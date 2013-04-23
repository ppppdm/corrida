package com.dorm.smartterminal.global.util;

import java.util.concurrent.locks.ReentrantLock;

public class WorkLocker {
    
    private Boolean hasTask = false;
    private final ReentrantLock lock = new ReentrantLock();
    
    /*
     * call this function to set hasTask true
     * if return true set hasTask true successful, if return false the hasTask already set true
     */
    public Boolean setHasTask(){
        Boolean re = false;
        lock.lock();
        
        if (!hasTask){
            hasTask = true;
            re = true;
        } 
        else {
            re = false;
        }
        
        lock.unlock();
        return re;
    }
    
    /*
     * call this function to set hasTask false
     * if return true set hasTask false successful, if return false the hasTask already set false
     */
    public Boolean setNoTask(){
        Boolean re = false;
        lock.lock();
        
        if (hasTask){
            hasTask = false;
            re = true;
        } 
        else {
            re = false;
        }
        
        lock.unlock();
        return re;
    }
}

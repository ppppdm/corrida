package com.dorm.smartterminal.global.util;

import java.util.Random;

/**
 * 
 * Tools for get random number.
 * 
 * @author andy liu
 * 
 */
public class RandomNumUtil {

    /**
     * 
     * get a random integer value
     * 
     * @return
     */
    public static int getRandomInteger() {

        return new Random().nextInt();

    }

}

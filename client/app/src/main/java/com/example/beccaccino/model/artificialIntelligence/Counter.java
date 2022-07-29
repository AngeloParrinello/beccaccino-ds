package com.example.beccaccino.model.artificialIntelligence;

/**
 * It defines a simple counter.
 */
public class Counter {

    private int counter;
    private static final int INITIALVALUE = 0;

    /**
     * Class constructor.
     */
    public Counter() {
        this.counter = INITIALVALUE;
    }

    /**
     * It return the next one integer.
     * 
     * @return the next one integer.
     */
    public int next() {
        return this.counter++;
    }

}

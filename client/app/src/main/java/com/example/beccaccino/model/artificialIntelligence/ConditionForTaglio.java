package com.example.beccaccino.model.artificialIntelligence;

/**
 * It defines the conditions in which AI is authorized to "tagliare", (play
 * briscola when the suit of the round is not equals to the briscola).
 */
public interface ConditionForTaglio {

    /**
     * It checks if there are conditions to "tagliare" or not.
     * 
     * @return true if there is the possibility of "tagliare", false otherwise.
     */
    boolean areRespected();

}

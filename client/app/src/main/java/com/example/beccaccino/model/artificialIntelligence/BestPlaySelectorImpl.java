package com.example.beccaccino.model.artificialIntelligence;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.example.beccaccino.model.entities.BeccaccinoBunchOfCards;
import com.example.beccaccino.model.entities.BunchOfCards;
import com.example.beccaccino.model.entities.ItalianCard;
import com.example.beccaccino.model.entities.Play;
import com.example.beccaccino.model.entities.PlayImpl;
import com.example.beccaccino.model.entities.ItalianCard.Suit;
import com.example.beccaccino.model.entities.ItalianCard.Value;
import com.example.beccaccino.model.logic.Round;

/**
 * It defines an implementation of "BestPlaySelector".
 */
public class BestPlaySelectorImpl implements BestPlaySelector {

    private final GameAnalyzer gameAnalyzer;

    /**
     * Class constructor.
     * 
     * @param gameAnalyzer is the game analyzer of AI.
     */
    public BestPlaySelectorImpl(final GameAnalyzer gameAnalyzer) {
        this.gameAnalyzer = gameAnalyzer;
    }

    /**
     * {@inheritDoc}
     */
    public Play doTheBestPlayFrom(final List<ItalianCard> listOfCards) {
        //listOfCards cannot be empty
        final ItalianCard myCard;
        Optional<String> message = Optional.empty();
        List<ItalianCard> myBestCards = new LinkedList<>();
        int max = 0;
        for (ItalianCard card : listOfCards) {
            int temp = this.gameAnalyzer.getWinningTeamProbability(card);
            if (temp > max) {
                max = temp;
                myBestCards = new LinkedList<>();
                myBestCards.add(card);
            } else if (temp == max) {
                myBestCards.add(card);
            }
        }
        final BunchOfCards bunchOfMyBestCards = new BeccaccinoBunchOfCards(myBestCards);
        if (areWinnerCard(myBestCards)) {
            final BunchOfCards myCardsWithMostPoints = new BeccaccinoBunchOfCards(
                    bunchOfMyBestCards.getCardsWithMostPoints());
            myCard = myCardsWithMostPoints.getHighestCards().get(0); 
            if (iVoloIn(myCard.getSuit())) {
                message = Optional.of("VOLO");
            }
        } else {
            return this.playLiscio(listOfCards);
        }
        return new PlayImpl(myCard, message);
    }


    //this method is called when the conditions for cutting are respected. 
    //Look at ConditionForTaglio.
    /**
     * {@inheritDoc}
     */
    public Play doTheBestTaglio() {
        final Suit briscola = this.gameAnalyzer.getBriscola();
        final Play myPlay;
        final Round currentRound = this.gameAnalyzer.getCurrentRound();
        final BunchOfCards bunchOfCards = new BeccaccinoBunchOfCards(currentRound.getPlayableCards());
        List<ItalianCard> myBriscole = bunchOfCards.getCardsOfSuit(briscola);
        final BunchOfCards setOfBriscole = new BeccaccinoBunchOfCards(myBriscole);
        if (hasTempWinnerTaglio()) {
            final ItalianCard winningCard = currentRound.getWinningPlay().get().getCard();
            myBriscole = this.compareWithMyCards(winningCard);
        }
        if (myBriscole.isEmpty()) {
            myPlay = this.playLiscio(currentRound.getPlayableCards());
        } else {
            final BunchOfCards briscoleWithMostPoints = new BeccaccinoBunchOfCards(
                    setOfBriscole.getCardsWithMostPoints());
            ItalianCard myCard = briscoleWithMostPoints.getLowestCards().get(0);
            myPlay = new PlayImpl(myCard, Optional.<String>empty());
        }
        return myPlay;
    }

    /**
     * It allows to play the best card from the set of cards passed by
     * parameter.
     * 
     * @param listOfCards is a list of cards.
     * @return the play i have done.
     */
    private Play playLiscio(final List<ItalianCard> listOfCards) {
        final BunchOfCards bunchOfCards = new BeccaccinoBunchOfCards(listOfCards);
        final Round currentRound = this.gameAnalyzer.getCurrentRound();
        Optional<String> message = Optional.empty();
        ItalianCard myCard = bunchOfCards.getLowestCards().get(0);
        if (currentRound.hasJustStarted()) {
            final List<ItalianCard> listOfTwo = bunchOfCards.getCardsOfValue(Value.DUE);
            //the AI "​​Bussi" is managed here
            if (!listOfTwo.isEmpty()) {
                final Suit suitBusso = listOfTwo.get(0).getSuit();
                if (!iVoloIn(suitBusso)) {
                    myCard = bunchOfCards.getLowestCardOfSuit(suitBusso).get();
                    message = Optional.of("BUSSO");
                }
            } else if (iVoloIn(myCard.getSuit())) {
                message = Optional.of("VOLO");
            }
        }
        return new PlayImpl(myCard, message);
    }

    /**
     * It compares the card that is winning the round with those of the AI.
     * 
     * @param winningCard is the card that is winning the round now.
     * @return a list of AI cards with higher value than the card that is
     * winning the round.
     */
    private List<ItalianCard> compareWithMyCards(final ItalianCard winningCard) {
        final Suit suit = winningCard.getSuit();
        List<ItalianCard> myBetterCardThan = new LinkedList<>();
        final Round currentRound = this.gameAnalyzer.getCurrentRound();
        final BunchOfCards bunchOfCards = new BeccaccinoBunchOfCards(currentRound.getPlayableCards());
        final BeccaccinoCardComparator comparator = new BeccaccinoCardComparator();
        final List<ItalianCard> cardsOfRoundSuit = bunchOfCards.getCardsOfSuit(suit);
        if (!cardsOfRoundSuit.isEmpty()) {
            for (ItalianCard card : cardsOfRoundSuit) {
                if (comparator.compare(card, winningCard) > 0) {
                    myBetterCardThan.add(card);
                }
            }
        }
        // could be an empty list in case the enemy has cut and the AI ​​cannot cut
        return myBetterCardThan;
    }

    /**
     * It checks the list of cards passed by parameters will all definitely win
     * the round.
     * 
     * @param cards - list of cards to consider.
     * @return boolean if are all winner card, false otherwise.
     */
    private boolean areWinnerCard(final List<ItalianCard> cards) {
        for (ItalianCard card : cards) {
            if (this.gameAnalyzer.getWinningTeamProbability(card) != 100) {
                return false;
            }
        }
        return true;
    }

    /**
     * It verifies that the AI has only one card of a suit.
     * 
     * @param suit - the suit to check.
     * @return true if "has volo" in the suit, false otherwise.
     */
    private boolean iVoloIn(final Suit suit) {
        final Round currentRound = this.gameAnalyzer.getCurrentRound();
        //the round does not have to be started for "Volo"
        if (currentRound.hasJustStarted()) {
            final BunchOfCards bunchOfCards = new BeccaccinoBunchOfCards(currentRound.getPlayableCards());
            //if it's the last card of the suit I have in my hand
            return bunchOfCards.getCardsOfSuit(suit).size() == 1;
        }
        return false;
    }

    private boolean hasTempWinnerTaglio() {
        final Round currentRound = this.gameAnalyzer.getCurrentRound();
        if (!currentRound.hasJustStarted()) {
            final ItalianCard tempWinner = currentRound.getWinningPlay().get().getCard();
            if (this.gameAnalyzer.isTaglio(tempWinner)) {
                return true;
            }
        }
        return false;
    }

}

package com.example.beccaccino.model.artificialIntelligence;

import com.example.beccaccino.model.entities.*;
import com.example.beccaccino.model.entities.ItalianCard.Suit;
import com.example.beccaccino.model.logic.Round;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * It defines a basic analyzer of a game.
 */
public class GameBasicAnalyzer implements GameAnalyzer {

    /**
     * This field represents the position of the rightmost player.
     */
    protected static final int RIGHT = 0;
    /**
     * This field represents the position of the upper player.
     */
    protected static final int TEAMMATE = 1;
    /**
     * This field represents the position of the leftmost player.
     */
    protected static final int LEFT = 2;
    /**
     * This field represents the position of the human player.
     */
    protected static final int ME = 3;
    /**
     * It serves to verify if a player is the first in the round.
     */
    protected static final int FIRST = 0;
    /**
     * It serves to verify if a player is the second in the round.
     */
    protected static final int SECOND = 1;
    /**
     * It serves to verify if a player is the third in the round.
     */
    protected static final int THIRD = 2;
    private final List<ItalianCard> handCard;
    private final List<Partecipant> allPlayers;
    private final List<ItalianCard> remainingCards;
    private final List<Play> allPlays;
    private final List<Round> roundPlayed;
    private Round currentRound;
    private Round lastRound;
    private Suit briscola;

    /**
     * Class constructor.
     *
     * @param handCards is the AI's hand.
     */
    public GameBasicAnalyzer(final List<ItalianCard> handCards) {
        this.handCard = handCards;
        this.remainingCards = this.initializeRemainingCards(handCards);
        this.allPlayers = this.initializePlayers();
        this.allPlays = new LinkedList<>();
        this.roundPlayed = new LinkedList<>();
        this.briscola = null;
    }

    /**
     * {@inheritDoc}
     */
    public void observePlays(final Round currentRound) {
        this.currentRound = currentRound;
        this.roundPlayed.add(this.currentRound);
        //if it is not the first of the round look at the plays made
        if (!currentRound.hasJustStarted()) {
            List<Play> roundPlays = this.currentRound.getPlays();
            final int alreadyPlayed = roundPlays.size();
            final int firstPlay = ME - alreadyPlayed;
            this.observePlaysCurrentRound(firstPlay, LEFT);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateLastRound() {
        if (hastheMatchStarted()) { //if it is not the first round of the game
            final List<Play> roundPlays = this.lastRound.getPlays();
            final Play myLastPlay = this.allPlays.get(this.allPlays.size() - 1);
            // play done by RIGHT player in last round
            final int rightPlay = roundPlays.indexOf(myLastPlay) + 1;
            final int lastPlay = roundPlays.size();
            this.observePlaysLastRound(rightPlay, lastPlay);
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getWinningTeamProbability(final ItalianCard card) {
        final int probability = 100;
        if (isTeammateTempWinner()) {
            final ItalianCard cardTeammate = this.currentRound.getWinningPlay().get().getCard();
            if (willWinTheRound(cardTeammate)) {
                return probability; // 100
            }
        } else if (myRoundPositionIs(FIRST) && hasPlayerTheBestCardOf(TEAMMATE, card.getSuit())) {
            return probability;
        } else if (myRoundPositionIs(SECOND) && hasPlayerTheBestCardOf(TEAMMATE, this.currentRound.getSuit().get())) {
            return probability;
        } else if (isEnemyTempWinner()) { // if the enemy is winning
            final BeccaccinoCardComparator comparator = new BeccaccinoCardComparator();
            final ItalianCard tempWinner = this.currentRound.getWinningPlay().get().getCard();
            if (!card.getSuit().equals(tempWinner.getSuit()) || comparator.compare(tempWinner, card) > 0) {
                return 0;
            }
        }
        return this.getWinningProbabilityOf(card);
    }

    /**
     * {@inheritDoc}
     */
    public void addMyPlay(final Play play) {
        this.lastRound = this.currentRound;
        this.allPlays.add(play);
    }

    /**
     * {@inheritDoc}
     */
    public Suit getBriscola() {
        return this.briscola;
    }

    /**
     * It set the briscola once it was chosen.
     *
     * @param briscola is the briscola of match.
     */
    public void setBriscola(final Suit briscola) {
        this.briscola = briscola;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTeammateTempWinner() {
        if (!this.currentRound.hasJustStarted()) {
            return this.allPlayers.get(TEAMMATE).getPlays().contains(this.currentRound.getWinningPlay().get());
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean willWinTheRound(final ItalianCard tempWinnerCard) {
        if (!this.currentRound.hasJustStarted()) {
            final ItalianCard winnerCard = this.currentRound.getWinningPlay().get().getCard();
            if (tempWinnerCard.equals(winnerCard)) {
                if (isTaglio(tempWinnerCard)) {
                    return true;
                }
            }
        }
        return this.getWinningProbabilityOf(tempWinnerCard) == 100;
    }

    /**
     * {@inheritDoc}
     */
    public List<ItalianCard> getRemainingCards() {
        return this.remainingCards;
    }

    /**
     * {@inheritDoc}
     */
    public Round getCurrentRound() {
        return this.currentRound;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTaglio(final ItalianCard card) {
        if (!this.currentRound.hasJustStarted()) {
            final Suit roundSuit = this.currentRound.getSuit().get();
            return card.getSuit().equals(briscola) && !roundSuit.equals(card.getSuit());
        }
        return false;
    }

    // ***** UTILITY *********************//

    /**
     * It allows to watch the plays made in the current round.
     *
     * @param firstPlay is the first play of round
     * @param lastPlay  is the last play made in the current round
     */
    protected void observePlaysCurrentRound(final int firstPlay, final int lastPlay) {
        List<Play> roundPlays = this.currentRound.getPlays();
        final Counter counter = new Counter();
        for (int indexPlay = firstPlay; indexPlay <= lastPlay; indexPlay++) {
            final Play playDone = roundPlays.get(counter.next());
            final Optional<String> message = playDone.getMessage();
            if (message.isPresent() && message.get().equals("BUSSO")) {
                this.playerHasBusso(indexPlay);
            }
            this.removeAndAdd(indexPlay, playDone);
        }
    }

    /**
     * It allows to watch the plays made in the current round.
     *
     * @param rightPlay is the first play of round
     * @param lastPlay  is the last play made in the current round
     */
    protected void observePlaysLastRound(final int rightPlay, final int lastPlay) {
        // counter = 0 --> RIGHT
        final Counter counter = new Counter();
        final List<Play> roundPlays = this.lastRound.getPlays();
        for (int i = rightPlay; i < lastPlay; i++) {
            this.removeAndAdd(counter.next(), roundPlays.get(i));
        }
    }

    /**
     * It checks if a enemy player is the temporary winner of the round.
     *
     * @return true if he is winning, false otherwise.
     */
    protected boolean isEnemyTempWinner() {
        return !this.currentRound.hasJustStarted() && !this.isTeammateTempWinner();
    }

    /**
     * It allows to evaluate the possibility that a card could win the round,
     * without considering the cards that were played in the current round.
     *
     * @param card is the card to evaluate.
     * @return the probability of winning.
     */
    protected int getWinningProbabilityOf(final ItalianCard card) {
        final BeccaccinoCardComparator comparator = new BeccaccinoCardComparator();
        int probability = 100;
        if (!this.remainingCards.isEmpty()) {
            final BunchOfCards bunchOfCards = new BeccaccinoBunchOfCards(this.remainingCards);
            final List<ItalianCard> cardsOf = bunchOfCards.getCardsOfSuit(card.getSuit());
            cardsOf.add(card);
            cardsOf.sort(comparator);
            final List<ItalianCard> remainingCardsWithMoreValue = this.betterRemainingCard(card, cardsOf);
            if (!havePlayerAllCards(TEAMMATE, remainingCardsWithMoreValue)) { // teammate
                probability = this.observeProbabilityOfEnemies(remainingCardsWithMoreValue);
            }
        }
        return probability;
    }

    /**
     * It is used to understand the cards that are better than the card passed
     * as a parameter and that could be played this turn.
     *
     * @param card    is the card to evaluate
     * @param cardsOf are cards of the same suit as the one passed as a
     *                parameter
     * @return a list of better card than than the card passed as parameter
     */
    protected List<ItalianCard> betterRemainingCard(final ItalianCard card, final List<ItalianCard> cardsOf) {
        final List<ItalianCard> remainingCardsWithMoreValue = new LinkedList<>();
        final BeccaccinoCardComparator comparator = new BeccaccinoCardComparator();
        for (ItalianCard cardOf : cardsOf) {
            if (comparator.compare(cardOf, card) > 0) {
                remainingCardsWithMoreValue.add(cardOf);
            }
        }
        return remainingCardsWithMoreValue;
    }

    /**
     * It allows to update conditions of game after a "Busso".
     *
     * @param player is the player that has "Busso".
     */
    protected void playerHasBusso(final int player) {
        if (!this.currentRound.hasJustStarted()) {
            final Suit roundSuit = this.currentRound.getSuit().get();
            final List<ItalianCard> allRemainingCard = new LinkedList<>();
            allRemainingCard.addAll(this.remainingCards);
            allRemainingCard.addAll(this.handCard);
            final BunchOfCards bunchOfCards = new BeccaccinoBunchOfCards(allRemainingCard);
            final List<ItalianCard> cardsOf = bunchOfCards.getCardsOfSuit(roundSuit);
            if (cardsOf.size() >= 2) {
                final BeccaccinoCardComparator comparator = new BeccaccinoCardComparator();
                cardsOf.sort(comparator);
                ItalianCard card = cardsOf.get(cardsOf.size() - 2);
                for (Partecipant partecipant : this.allPlayers) {
                    partecipant.setProbabilityOf(card, 0);
                }
                this.allPlayers.get(player).setProbabilityOf(card, 100);
            }
            //otherwise it does nothing because the player has "Busso" at random
        }
    }

    /**
     * It allows to update the condition of match after a play done.
     *
     * @param player is the player that have done the play.
     * @param play   is the play done.
     */
    protected void removeAndAdd(final int player, final Play play) {
        this.remainingCards.remove(play.getCard());
        this.allPlays.add(play); // add the play
        this.allPlayers.get(player).addPlay(play);
        for (Partecipant partecipant : this.allPlayers) {
            partecipant.removeCard(play.getCard());
        }
    }

    /**
     * It serves to understand the position in the round of the AI.
     *
     * @param position is the position to check.
     * @return true if the position in the round is equal to that passed by
     * parameter.
     */
    protected boolean myRoundPositionIs(final int position) {
        return this.currentRound.getPlays().size() == position;
    }

    /**
     * It verifies that the game has started.
     *
     * @return true if the match is started, false otherwise.
     */
    protected boolean hastheMatchStarted() {
        return !this.roundPlayed.isEmpty();
    }

    /**
     * It allows to understand if the teammate "has busso" in a specific suit.
     *
     * @param player is the player to consider
     * @param suit   is the suit to consider
     * @return true if teammate "has busso" in the suit, false otherwise
     */
    protected boolean hasPlayerTheBestCardOf(final int player, final Suit suit) {
        final BunchOfCards bunchOfCards = new BeccaccinoBunchOfCards(this.remainingCards);
        if ((bunchOfCards.getHighestCardOfSuit(suit).isPresent())) {
            final ItalianCard bestCardOf = bunchOfCards.getHighestCardOfSuit(suit).get();
            return hasPlayerCard(player, bestCardOf);
        }
        return false;
    }

    /**
     * It returns the last round.
     *
     * @return the last round.
     */
    protected Round getLastRound() {
        return this.lastRound;
    }

    /**
     * It returns all players.
     *
     * @return all players.
     */
    protected List<Partecipant> getAllPlayer() {
        return this.allPlayers;
    }

    /**
     * It checks if a player has all the cards.
     *
     * @param player             is the player to consider.
     * @param cardsWithMoreValue are the cards to evaluate.
     * @return true if player has all the cards, false otherwise.
     */
    private boolean havePlayerAllCards(final int player, final List<ItalianCard> cardsWithMoreValue) {
        for (ItalianCard card : cardsWithMoreValue) {
            if (!hasPlayerCard(player, card)) {
                return false;
            }
        }
        return true;
    }

    /**
     * It checks if a player has a card.
     *
     * @param player is the player to consider
     * @param card   is the card to consider
     * @return true if player has the card
     */
    private boolean hasPlayerCard(final int player, final ItalianCard card) {
        return this.allPlayers.get(player).getProbabilityOf(card) == 100;
    }

    /**
     * It allows to evaluate the probabiity of winning the round consider the
     * probability of a card that is part of a set of cards better than the card
     * that is been calculated now the probability of winning.
     *
     * @param betterCards is a list of card with a better value than card that
     *                    is been considered
     * @return the probability of winning the round
     */
    private int observeProbabilityOfEnemies(final List<ItalianCard> betterCards) {
        int winProbability = 100;
        int enemiesProbability = 0;
        for (ItalianCard card : betterCards) {
            if (myRoundPositionIs(FIRST)) {
                enemiesProbability = this.allPlayers.get(RIGHT).getProbabilityOf(card)
                        + this.allPlayers.get(LEFT).getProbabilityOf(card);
            } else if (myRoundPositionIs(SECOND) || myRoundPositionIs(THIRD)) {
                enemiesProbability = this.allPlayers.get(RIGHT).getProbabilityOf(card);
            }
            winProbability = winProbability - enemiesProbability;
        }
        if (winProbability < 0) {
            winProbability = 0;
        }
        return winProbability;
    }

    /**
     * It initializes the list of participants in the match.
     *
     * @return the list of participants in the match.
     */
    private List<Partecipant> initializePlayers() {
        final List<Partecipant> allPlayers = new LinkedList<>();
        for (int i = RIGHT; i <= ME; i++) {
            Partecipant player = new PartecipantImpl(this.remainingCards);
            allPlayers.add(player);
        }
        return allPlayers;
    }

    /**
     * It initializes playable cards from other players.
     *
     * @param handCards is the AI's hand
     * @return a list of playable cards from other players
     */
    private List<ItalianCard> initializeRemainingCards(final List<ItalianCard> handCards) {
        final List<ItalianCard> remainingCards = new LinkedList<>();
        final ItalianCardsDeck deck = new ItalianCardsDeckImpl();
        while (deck.remainingCards() > 0) {
            ItalianCard card = deck.drawCard();
            if (!handCards.contains(card)) {
                remainingCards.add(card);
            }
        }
        return remainingCards;
    }

}

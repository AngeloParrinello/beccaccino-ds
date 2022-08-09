package com.example.beccaccino.model.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.beccaccino.model.entities.BeccaccinoBunchOfCards;
import com.example.beccaccino.model.entities.BunchOfCards;
import com.example.beccaccino.model.entities.ItalianCard;
import com.example.beccaccino.model.entities.ItalianCard.Suit;
import com.example.beccaccino.model.entities.Play;

/**
 * This is a round of the Italian card game "Beccaccino".
 * @see <a href="Marafone Beccaccino">https://it.wikipedia.org/wiki/Marafone_Beccacino</a>
 */
public class BeccaccinoRound extends RoundTemplate {
    private final Suit briscola;
    private final int numberOfPlayers;

    /**
     * {@inheritDoc}.
     * @param briscola - the briscola suit.
     */
    public BeccaccinoRound(final TurnOrder turnOrder, final Suit briscola) {
        super(turnOrder);
        this.briscola = briscola;
        this.numberOfPlayers = turnOrder.getPlayers().size();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isOver() {
        return this.getPlays().size() == numberOfPlayers;
    }

    /**
     * {@inheritDoc}
     * If the round isn't over yet, returns the play that is currently winning the round.
     * The hierarchy that determines which card wins is the following:
     * 1)Card with greatest value among cards of briscola suit.
     * 2)Card with greatest value among cards of this round dominant suit.
     */
    public Optional<Play> getWinningPlay() {
        if (this.hasJustStarted()) {
            return Optional.empty();
        }
        final BunchOfCards playedCards = new BeccaccinoBunchOfCards(this.getPlayedCards());
        if (this.hasJustStarted()) {
            return Optional.empty();
        }
        Optional<ItalianCard> winningCard = playedCards.getHighestCardOfSuit(this.briscola);
        if (winningCard.isPresent()) {
            return this.getPlayThatContains(winningCard.get());
        }

        winningCard = playedCards.getHighestCardOfSuit(this.getSuit().get());

        return this.getPlayThatContains(winningCard.get());
    }

    /**
     * If the player is the first of the round, all cards are playable. 
     * If he isn't, he must play only cards of the dominant suit of the round. 
     * If he hasn't cards of dominant suit, all cards are playable.
     * 
     * @return list of playable cards.
     */
    public List<ItalianCard> getPlayableCards() {
        final List<ItalianCard> allCards = this.getCurrentPlayer().getHand().getCards();
        final List<ItalianCard> playableCards = new ArrayList<>();

        if (this.hasJustStarted()) {
            return allCards;
        }
        for (ItalianCard card : allCards) {
            if (card.getSuit().equals(this.getSuit().get())) {
                playableCards.add(card);
            }
        }
        if (playableCards.isEmpty()) {
            return allCards;
        }
        return playableCards;
    }

    /**
     * This is an utility method returning the suit of this round.
     * 
     * @return an optional containing the dominant suit of this round (namely
     * the suit of the first card played in this round) if present, or an empty
     * optional otherwise.
     */
    public Optional<Suit> getSuit() {
        if (this.hasJustStarted()) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.getPlays().get(0).getCard().getSuit());
    }

    /**
     * Only the first player of the round can send messages.
     * {@inheritDoc}
     */
    public List<Optional<String>> getSendableMessages(final ItalianCard card) {
        final List<Optional<String>> sendableMessages = new ArrayList<>();
        sendableMessages.add(Optional.<String>empty());
        if (this.hasJustStarted()) {
            sendableMessages.add(Optional.of("BUSSO"));
            sendableMessages.add(Optional.of("STRISCIO"));
            sendableMessages.add(Optional.of("VOLO"));
        }
        return sendableMessages;
    }

    /**
     * {@inheritDoc}
     */
    protected void checkPlay(final Play play) {
        final ItalianCard card = play.getCard();
        if (!this.getPlayableCards().contains(card)) {
            throw new IllegalArgumentException("Can't play this card: " + card);
        }
        if (!this.getSendableMessages(card).contains(play.getMessage())) {
            throw new IllegalArgumentException("Can't send this message now: " + play.getMessage().get());
         }
    }

    /**
     * This is an utility method looking for the play that contains a certain
     * card.
     * 
     * @param card - the card to be searched among the plays
     * @return an optional containing the play containing given card if present,
     * an empty optional otherwise
     */
    private Optional<Play> getPlayThatContains(final ItalianCard card) {
        if (this.hasJustStarted()) {
            return Optional.empty();
        }
        for (Play play : this.getPlays()) {
            if (play.getCard().equals(card)) {
                return Optional.ofNullable(play);
            }
        }
        return Optional.empty();
    }

    public List<ItalianCard> getPlayedCards() {
        final List<ItalianCard> cards = new ArrayList<>();
        for (Play play : this.getPlays()) {
            cards.add(play.getCard());
        }
        return cards;
    }
}

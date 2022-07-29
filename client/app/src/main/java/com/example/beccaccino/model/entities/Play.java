package com.example.beccaccino.model.entities;

import java.util.Optional;

/**
 * A play is made by a card along with an optional message. It's an immutable object.
 */
public interface Play {
    /**
     * @return the card played
     */
    ItalianCard getCard();

    /**
     * @return an optional containing the message sent, or an empty optional if
     * no message should be sent
     */
    Optional<String> getMessage();
}

package com.mergermarket.card.cardproperty;

import com.mergermarket.card.cardproperty.CardProperty;
import com.mergermarket.exception.InvalidSuitException;

/**
 * Domain class for storing the suits of cards.
 */
public class Suit extends CardProperty {

    private char suit;

    /**
     * Valid values of suit.  In a production application we'd probably want to store these in
     * config rather than hard-coding them here.
     */
    private static final char[] validInputs = { 'D', 'H', 'c', 's' };

    /**
     * Default constructor.
     */
    public Suit() {
    }

    /**
     * Constructor with argument.
     *   @param input The character to which we'll set the suit.
     */
    public Suit(final char input) throws InvalidSuitException {
        if (isValid(input)) {
            setSuit(input);
        }
        else {
            throw new InvalidSuitException("Invalid suit " + input + " passed to Suit constructor");
        }
    }

    /**
     * Checks whether the input is a valid suit.
     *   @param input The character to validate.
     *   @return true if the character is a valid suit, false otherwise.
     */
    public boolean isValid(final char input) {
        return isValid(input, validInputs);
    }

    /**
     * Return all the valid suits.
     *   @return A char array of the valid suits.
     */
    public static char[] getValidInputs() {
        return validInputs;
    }

    public char getSuit() {
        return suit;
    }

    public void setSuit(final char suit) {
        this.suit = suit;
    }
}

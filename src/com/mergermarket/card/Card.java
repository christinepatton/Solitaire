package com.mergermarket.card;

import com.mergermarket.card.cardproperty.FaceValue;
import com.mergermarket.card.cardproperty.Suit;
import com.mergermarket.exception.InvalidFaceValueException;
import com.mergermarket.exception.InvalidSuitException;

/**
 * Domain class for storing cards.  Relies on the Suit and FaceValue domain classes.
 */
public class Card {

    private Suit suit;
    private FaceValue faceValue;
    private boolean faceUp;


    /**
     * Default constructor.
     */
    public Card() {
    }

    /**
     * Constructor with suit and face value arguments.  Validation of the suit and face value is done
     * in the Suit and FaceValue constructors.
     *   @param suit The suit this card should have.
     *   @param faceValue The face value this card should have.
     */
    public Card(final char suit, final char faceValue) throws InvalidSuitException, InvalidFaceValueException {
        this.suit = new Suit(suit);
        this.faceValue = new FaceValue(faceValue);
        this.faceUp = false;
    }

    /**
     * Constructor with single argument for card value.  Validation of the suit and face value inherent
     * in the card value is done in the Suit and FaceValue constructors.
     *   @param cardValue The value (suit + face value) the card should have.
     */
    public Card(final String cardValue) throws InvalidSuitException, InvalidFaceValueException {
        char suit = cardValue.charAt(0);
        char faceValue = cardValue.charAt(1);

        this.suit = new Suit(suit);
        this.faceValue = new FaceValue(faceValue);
        this.faceUp = false;
    }

    /**
     * Combine the suit and face value to return the card's value, e.g. "c3".
     *  @return A String representing the card's value.
     */
    public String getValue() {
        return String.valueOf(suit.getSuit()) + String.valueOf(faceValue.getFaceValue());
    }

    @Override
    /**
     * Useful shorthand for determining if one Card is the same as another.  Two cards
     * are considered the same if they have the same value (suit + face value).
     *  @return true if the cards are the same, false if not.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;

        if (this.getValue().equals(card.getValue())) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }
}

package com.mergermarket.deck;

import com.mergermarket.card.Card;
import com.mergermarket.card.cardproperty.FaceValue;
import com.mergermarket.card.cardproperty.Suit;
import com.mergermarket.exception.InvalidFaceValueException;
import com.mergermarket.exception.InvalidSuitException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class to manage a deck of cards.  Really just sort of a CardFactory.
 */
public class Deck {

    private List<Card> cards;

    /**
     * Default constructor.  Create the set of cards.
     */
    public Deck() throws InvalidSuitException, InvalidFaceValueException {
        cards = new ArrayList<Card>();
        for (char suit : Suit.getValidInputs()) {
            for (char faceValue : FaceValue.getValidInputs()) {
                cards.add(new Card(suit, faceValue));
            }
        }
    }

    /**
     * Randomise the order of the cards.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    public List<Card> getCards() {
        return cards;
    }
}

package com.mergermarket.deck;

import com.mergermarket.card.Card;
import com.mergermarket.card.cardproperty.FaceValue;
import com.mergermarket.card.cardproperty.Suit;
import com.mergermarket.exception.InvalidFaceValueException;
import com.mergermarket.exception.InvalidSuitException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for Deck domain class.
 */
public class DeckTest {

    @Test
    /* Very basic test to see that we can create and then access the required number of cards.
     */
    public void testGetCards() throws InvalidSuitException, InvalidFaceValueException {
        int numValidSuits = Suit.getValidInputs().length;
        int numValidFaceValues = FaceValue.getValidInputs().length;

        Deck deck = new Deck();
        List<Card> cards = deck.getCards();
        assertEquals(numValidSuits * numValidFaceValues, cards.size());
    }

    @Test
    /* Test that shuffle changes the order of the cards.
     */
    public void testShuffle() throws InvalidSuitException, InvalidFaceValueException {
        Deck deck = new Deck();

        // Make a copy of the cards before shuffling.
        List<Card> unshuffled = new ArrayList<>();
        for (Card c : deck.getCards()) {
            unshuffled.add(c);
        }

        // Now shuffle.
        deck.shuffle();
        List<Card> shuffled = deck.getCards();

        // Look for a card, any card, that's not in the same place in the array that it used to be.
        for (int i = 0; i < unshuffled.size(); i++) {
            Card before = unshuffled.get(i);
            Card after = shuffled.get(i);
            if (!(before.equals(after))) {
                return;
            }
        }

        // If we've gone all the way through the deck and the cards are all the same, fail.
        // (It may be theoretically possible that shuffling the deck could result in no changes to
        // the card ordering, but I think that possibility is remote enough that we can discount
        // it here.)
        fail("Order of cards did not change after shuffle");
    }

}
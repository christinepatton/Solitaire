package com.mergermarket.card;

import com.mergermarket.exception.InvalidFaceValueException;
import com.mergermarket.exception.InvalidSuitException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for Card domain class.
 */
public class CardTest {

    @Test
    /* Very basic test to see if we can put in valid input and subsequently get that input back out.
     */
    public void testGetValue() throws InvalidSuitException, InvalidFaceValueException {
        Card card = new Card('c', '3');
        assertEquals(card.getValue(), "c3");

        card = new Card("H4");
        assertEquals(card.getValue(), "H4");
    }

    @Test
    /* Test the overridden equals operator.  If two cards have the same suit and face value, they should
     * be considered equal.  If not, they shouldn't.
     */
    public void testEquals() throws InvalidSuitException, InvalidFaceValueException {
        Card card = new Card('c', '5');
        assertTrue(card.equals(card));

        Card sameCard = new Card('c', '5');
        assertTrue(card.equals(sameCard));

        Card differentCard = new Card('c', '6');
        assertFalse(card.equals(differentCard));
    }

}
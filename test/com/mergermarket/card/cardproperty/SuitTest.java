package com.mergermarket.card.cardproperty;

import com.mergermarket.exception.InvalidSuitException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for Suit domain class.
 */
public class SuitTest {

    @Test
    /* Very basic test to see if we can put in valid input and subsequently get that input back out.
     */
    public void testGetSuit() throws InvalidSuitException {
        Suit suit = new Suit('H');
        assertEquals('H', suit.getSuit());
    }

    @Test(expected = com.mergermarket.exception.InvalidSuitException.class)
    /* Test that bad input to the constructor results in an exception.
     */
    public void testInvalidConstructorInput() throws InvalidSuitException {
        Suit suit = new Suit('X');
    }

    @Test
    /* Test that isValid does the right thing with both valid and invalid input.
     */
    public void testIsValid() {
        Suit suit = new Suit();

        // Test with valid input
        assertEquals(true, suit.isValid('D'));

        // Test with invalid input
        assertEquals(false, suit.isValid('S'));
    }

    @Test
     /* Test that getValidInputs returns the expected list of chars.
     */
    public void testGetValidInputs() {
        char[] expectedValidInputs = { 'D', 'H', 'c', 's' };
        assertArrayEquals(expectedValidInputs, Suit.getValidInputs());
    }

}
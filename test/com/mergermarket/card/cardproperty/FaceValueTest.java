package com.mergermarket.card.cardproperty;

import com.mergermarket.card.cardproperty.FaceValue;
import com.mergermarket.exception.InvalidFaceValueException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for FaceValue domain class.
 */
public class FaceValueTest {

    @Test
    /* Very basic test to see if we can put in valid input and subsequently get that input back out.
     */
    public void testGetFaceValue() throws InvalidFaceValueException {
        // Test with a letter
        FaceValue faceValue = new FaceValue('T');
        assertEquals('T', faceValue.getFaceValue());

        // Now test with a number
        faceValue = new FaceValue('2');
        assertEquals('2', faceValue.getFaceValue());
    }

    @Test(expected = com.mergermarket.exception.InvalidFaceValueException.class)
    /* Test that bad input to the constructor results in an exception.
     */
    public void testInvalidConstructorInput() throws InvalidFaceValueException {
        FaceValue faceValue = new FaceValue('B');
    }

    @Test
    /* Test that isValid does the right thing with both valid and invalid input.
     */
    public void testIsValid() {
        FaceValue faceValue = new FaceValue();

        // Test with valid input
        assertEquals(true, faceValue.isValid('3'));

        // Test with invalid input
        assertEquals(false, faceValue.isValid('S'));
    }

    @Test
     /* Test that getValidInputs returns the expected list of chars.
     */
    public void testGetValidInputs() {
        char[] expectedValidInputs = { 'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K' };
        assertArrayEquals(expectedValidInputs, FaceValue.getValidInputs());
    }
}
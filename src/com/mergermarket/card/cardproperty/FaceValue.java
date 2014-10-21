package com.mergermarket.card.cardproperty;

import com.mergermarket.exception.InvalidFaceValueException;

/**
 * Domain class for storing the face values of cards.
 */
public class FaceValue extends CardProperty {

    private char faceValue;

    /**
     * Valid values of faceValue.  In a production application we'd probably want to store these in
     * config rather than hard-coding them here.
     */
    private static final char[] validInputs = { 'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K' };

    /**
     * Default constructor.
     */
    public FaceValue() {
    }

    /**
     * Constructor with argument.
     *   @param input The character to which we'll set the face value.
     */
    public FaceValue(final char input) throws InvalidFaceValueException {
        if (isValid(input)) {
            setFaceValue(input);
        }
        else {
            throw new InvalidFaceValueException("Invalid face value " + input + " passed to FaceValue constructor");
        }
    }

    /**
     * Checks whether the input is a valid face value.
     *   @param input The character to validate.
     *   @return true if the character is a valid face value, false otherwise.
     */
    public boolean isValid(final char input) {
        return isValid(input, validInputs);
    }

    /**
     * Return all the valid face values.
     *   @return A char array of the valid face values.
     */
    public static char[] getValidInputs() {
        return validInputs;
    }

    public char getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(final char faceValue) {
        this.faceValue = faceValue;
    }
}

package com.mergermarket.card.cardproperty;

/**
 * Abstract domain class for properties of a card (currently suit and face value).
 */
public abstract class CardProperty {

    /**
     * Checks whether the input is a valid suit
     *   @param input The character to validate.
     *   @return true if the character is a valid suit, false otherwise.
     */
    public boolean isValid(final char input, final char[] validInputs) {
        /* We could implement binary search here instead of just linearly
           iterating through the array, but because we expect the number of items
           in the array to be relatively small, any performance boost would be minimal.

           Another option would be to use a HashMap which we populate at object
           creation time.
         */
        for (char t : validInputs) {
            if (t == input) {
                return true;
            }
        }
        return false;
    }
}

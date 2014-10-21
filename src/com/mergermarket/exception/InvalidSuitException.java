package com.mergermarket.exception;

/**
 * Exception thrown if the application sees an invalid suit.
 */
public class InvalidSuitException extends Exception {

    public InvalidSuitException(final String message) {
        super(message);
    }
}

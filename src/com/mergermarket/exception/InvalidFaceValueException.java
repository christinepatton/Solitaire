package com.mergermarket.exception;

/**
 * Exception thrown if the application sees an invalid face value.
 */
public class InvalidFaceValueException extends Exception {

    public InvalidFaceValueException(final String message) {
        super(message);
    }
}

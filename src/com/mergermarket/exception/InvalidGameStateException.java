package com.mergermarket.exception;

/**
 * Exception thrown if the game can't be initialised or gets into an "impossible" state.
 */
public class InvalidGameStateException extends Exception {

    public InvalidGameStateException(final String message) {
        super(message);
    }
}

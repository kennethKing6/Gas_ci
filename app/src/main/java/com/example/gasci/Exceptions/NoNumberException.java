package com.example.gasci.Exceptions;

/**
 * This class is only used when the owner of a gaz boutique has not enterred a number for
 * the future clients to be able to see it
 */
public class NoNumberException extends Exception {
    public NoNumberException(String msg) {
        super(msg);
    }
}

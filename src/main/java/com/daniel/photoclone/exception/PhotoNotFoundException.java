package com.daniel.photoclone.exception;
// Move exception outside as standalone class
public class PhotoNotFoundException extends RuntimeException {
    public PhotoNotFoundException(String message) {
        super(message);
    }
}

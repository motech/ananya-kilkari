package org.motechproject.ananya.kilkari.exceptions;

public class KilkariException extends Exception {

    public KilkariException() {
       super();
    }

    public KilkariException(String message) {
        super(message);
    }

    public KilkariException(String message, Throwable cause) {
        super(message, cause);
    }

    public KilkariException(Throwable cause) {
        super(cause);
    }
}

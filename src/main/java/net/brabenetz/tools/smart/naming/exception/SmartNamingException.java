package net.brabenetz.tools.smart.naming.exception;

public class SmartNamingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SmartNamingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SmartNamingException(final String message) {
        super(message);
    }
}